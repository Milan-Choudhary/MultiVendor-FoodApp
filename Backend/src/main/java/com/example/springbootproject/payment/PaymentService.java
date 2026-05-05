package com.example.springbootproject.payment;

import com.foodapp.fooddelivery.entity.Order;
import com.foodapp.fooddelivery.entity.OrderStatus;
import com.foodapp.fooddelivery.entity.PaymentStatus;
import com.foodapp.fooddelivery.exception.BadRequestException;
import com.foodapp.fooddelivery.exception.ResourceNotFoundException;
import com.foodapp.fooddelivery.exception.UnauthorizedActionException;
import com.foodapp.fooddelivery.notification.OrderNotificationService;
import com.foodapp.fooddelivery.payment.dto.PaymentRequest;
import com.foodapp.fooddelivery.payment.dto.PaymentResponse;
import com.foodapp.fooddelivery.repository.OrderRepository;
import com.foodapp.fooddelivery.security.AppUserPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final OrderNotificationService notificationService;

    @Transactional
    public PaymentResponse mockCharge(AppUserPrincipal principal, Long orderId, PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(principal.getId())) {
            throw new UnauthorizedActionException("You can only pay for your own orders");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Only newly created orders can be paid");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Order has already been paid");
        }
        if (request.amount().compareTo(order.getTotalPrice()) != 0) {
            throw new BadRequestException("Payment amount must match the order total");
        }

        boolean success = request.simulateSuccess() == null || request.simulateSuccess();
        if (success) {
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setPaymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            notificationService.publishOrderUpdate(order, "Mock payment completed successfully");
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setPaymentReference(null);
            notificationService.publishOrderUpdate(order, "Mock payment failed");
        }

        Order savedOrder = orderRepository.save(order);
        return PaymentResponse.builder()
                .orderId(savedOrder.getId())
                .paymentStatus(savedOrder.getPaymentStatus())
                .paymentReference(savedOrder.getPaymentReference())
                .message(success ? "Mock payment successful" : "Mock payment failed")
                .build();
    }
}
