package com.foodapp.fooddelivery.order;

import com.foodapp.fooddelivery.cart.CartService;
import com.foodapp.fooddelivery.cart.model.CartLine;
import com.foodapp.fooddelivery.cart.model.CartSnapshot;
import com.foodapp.fooddelivery.entity.MenuItem;
import com.foodapp.fooddelivery.entity.Order;
import com.foodapp.fooddelivery.entity.OrderItem;
import com.foodapp.fooddelivery.entity.OrderStatus;
import com.foodapp.fooddelivery.entity.PaymentStatus;
import com.foodapp.fooddelivery.entity.Restaurant;
import com.foodapp.fooddelivery.entity.RestaurantStatus;
import com.foodapp.fooddelivery.entity.Role;
import com.foodapp.fooddelivery.entity.User;
import com.foodapp.fooddelivery.exception.BadRequestException;
import com.foodapp.fooddelivery.exception.ResourceNotFoundException;
import com.foodapp.fooddelivery.exception.UnauthorizedActionException;
import com.foodapp.fooddelivery.notification.OrderNotificationService;
import com.foodapp.fooddelivery.order.dto.OrderResponse;
import com.foodapp.fooddelivery.order.dto.OrderStatusUpdateRequest;
import com.foodapp.fooddelivery.order.dto.PlaceOrderRequest;
import com.foodapp.fooddelivery.order.dto.VendorDecisionRequest;
import com.foodapp.fooddelivery.repository.MenuItemRepository;
import com.foodapp.fooddelivery.repository.OrderRepository;
import com.foodapp.fooddelivery.repository.RestaurantRepository;
import com.foodapp.fooddelivery.repository.UserRepository;
import com.foodapp.fooddelivery.security.AppUserPrincipal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final OrderNotificationService notificationService;

    @Transactional
    public OrderResponse placeOrder(AppUserPrincipal principal, PlaceOrderRequest request) {
        CartSnapshot cartSnapshot = cartService.getCartSnapshot(principal.getId());
        if (cartSnapshot.getRestaurantId() == null || cartSnapshot.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Restaurant restaurant = restaurantRepository.findById(cartSnapshot.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (restaurant.getStatus() != RestaurantStatus.OPEN) {
            throw new BadRequestException("Restaurant is not accepting orders");
        }

        List<Long> menuItemIds = cartSnapshot.getItems().stream()
                .map(CartLine::getMenuItemId)
                .toList();

        Map<Long, MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        if (menuItems.size() != menuItemIds.size()) {
            throw new BadRequestException("One or more menu items could not be found");
        }

        User customer = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Order order = Order.builder()
                .user(customer)
                .restaurant(restaurant)
                .status(OrderStatus.CREATED)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryAddress(request.deliveryAddress())
                .totalPrice(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (CartLine line : cartSnapshot.getItems()) {
            MenuItem menuItem = menuItems.get(line.getMenuItemId());
            if (!menuItem.isAvailable()) {
                throw new BadRequestException(menuItem.getName() + " is no longer available");
            }
            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new BadRequestException("Cart contains items from multiple restaurants");
            }

            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            items.add(OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(line.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .build());
        }

        order.setItems(items);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(principal.getId());
        notificationService.publishOrderUpdate(savedOrder, "Order created and awaiting payment");
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(Long customerId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getVendorOrders(Long vendorId) {
        return orderRepository.findByRestaurantOwnerIdOrderByCreatedAtDesc(vendorId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderForPrincipal(Long orderId, AppUserPrincipal principal) {
        return orderMapper.toResponse(getVisibleOrder(orderId, principal));
    }

    @Transactional
    public OrderResponse vendorDecision(Long orderId, AppUserPrincipal principal, VendorDecisionRequest request) {
        Order order = orderRepository.findByIdAndRestaurantOwnerId(orderId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this vendor"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Only newly created orders can be accepted or rejected");
        }

        if (Boolean.TRUE.equals(request.approved())) {
            if (order.getPaymentStatus() != PaymentStatus.PAID) {
                throw new BadRequestException("Payment must be completed before vendor acceptance");
            }
            order.setStatus(OrderStatus.ACCEPTED_BY_VENDOR);
            order.setRejectionReason(null);
            notificationService.publishOrderUpdate(order, "Vendor accepted the order");
        } else {
            order.setStatus(OrderStatus.REJECTED_BY_VENDOR);
            order.setRejectionReason(request.rejectionReason());
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }
            notificationService.publishOrderUpdate(order, "Vendor rejected the order");
        }

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateVendorOrderStatus(Long orderId,
                                                 AppUserPrincipal principal,
                                                 OrderStatusUpdateRequest request) {
        Order order = orderRepository.findByIdAndRestaurantOwnerId(orderId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this vendor"));

        OrderStatus requestedStatus = request.status();
        if (requestedStatus != OrderStatus.PREPARING
                && requestedStatus != OrderStatus.READY_FOR_PICKUP
                && requestedStatus != OrderStatus.CANCELLED) {
            throw new BadRequestException("Vendor can only set PREPARING, READY_FOR_PICKUP, or CANCELLED");
        }

        if (requestedStatus == OrderStatus.PREPARING
                && order.getStatus() != OrderStatus.ACCEPTED_BY_VENDOR
                && order.getStatus() != OrderStatus.PREPARING) {
            throw new BadRequestException("Order must be accepted before it can be prepared");
        }

        if (requestedStatus == OrderStatus.READY_FOR_PICKUP
                && order.getStatus() != OrderStatus.ACCEPTED_BY_VENDOR
                && order.getStatus() != OrderStatus.PREPARING) {
            throw new BadRequestException("Order must be accepted or preparing before pickup");
        }

        if (requestedStatus == OrderStatus.CANCELLED && order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Delivered orders cannot be cancelled");
        }

        order.setStatus(requestedStatus);
        notificationService.publishOrderUpdate(order, "Vendor updated the order status to " + requestedStatus);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Order getVisibleOrder(Long orderId, AppUserPrincipal principal) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (principal.getRole() == Role.CUSTOMER && order.getUser().getId().equals(principal.getId())) {
            return order;
        }
        if (principal.getRole() == Role.VENDOR && order.getRestaurant().getOwner().getId().equals(principal.getId())) {
            return order;
        }
        if (principal.getRole() == Role.DELIVERY
                && order.getDelivery() != null
                && order.getDelivery().getDeliveryPartner().getId().equals(principal.getId())) {
            return order;
        }

        throw new UnauthorizedActionException("You are not allowed to access this order");
    }
}
