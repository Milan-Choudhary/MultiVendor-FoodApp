package com.foodapp.fooddelivery.order.dto;

import com.foodapp.fooddelivery.entity.OrderStatus;
import com.foodapp.fooddelivery.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderResponse(
        Long id,
        Long userId,
        Long restaurantId,
        String restaurantName,
        OrderStatus status,
        PaymentStatus paymentStatus,
        BigDecimal totalPrice,
        String deliveryAddress,
        String paymentReference,
        String rejectionReason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<OrderItemResponse> items,
        DeliverySummaryResponse delivery) {
}
