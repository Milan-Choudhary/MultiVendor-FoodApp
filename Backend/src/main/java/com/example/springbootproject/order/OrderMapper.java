package com.foodapp.fooddelivery.order;

import com.foodapp.fooddelivery.delivery.dto.DeliveryResponse;
import com.foodapp.fooddelivery.entity.Delivery;
import com.foodapp.fooddelivery.entity.Order;
import com.foodapp.fooddelivery.entity.OrderItem;
import com.foodapp.fooddelivery.order.dto.DeliverySummaryResponse;
import com.foodapp.fooddelivery.order.dto.OrderItemResponse;
import com.foodapp.fooddelivery.order.dto.OrderResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .paymentReference(order.getPaymentReference())
                .rejectionReason(order.getRejectionReason())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(mapOrderItems(order.getItems()))
                .delivery(mapDeliverySummary(order.getDelivery()))
                .build();
    }

    public DeliveryResponse toDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrder().getId())
                .deliveryPartnerId(delivery.getDeliveryPartner().getId())
                .deliveryPartnerName(delivery.getDeliveryPartner().getName())
                .status(delivery.getStatus())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }

    private List<OrderItemResponse> mapOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }

    private DeliverySummaryResponse mapDeliverySummary(Delivery delivery) {
        if (delivery == null) {
            return null;
        }
        return DeliverySummaryResponse.builder()
                .id(delivery.getId())
                .deliveryPartnerId(delivery.getDeliveryPartner().getId())
                .deliveryPartnerName(delivery.getDeliveryPartner().getName())
                .status(delivery.getStatus())
                .build();
    }
}
