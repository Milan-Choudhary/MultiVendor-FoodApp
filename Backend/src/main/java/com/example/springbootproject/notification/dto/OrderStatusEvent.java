package com.foodapp.fooddelivery.notification.dto;

import com.foodapp.fooddelivery.entity.DeliveryStatus;
import com.foodapp.fooddelivery.entity.OrderStatus;
import com.foodapp.fooddelivery.entity.PaymentStatus;
import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record OrderStatusEvent(
        Long orderId,
        Long customerId,
        Long vendorId,
        Long restaurantId,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        DeliveryStatus deliveryStatus,
        String message,
        OffsetDateTime updatedAt) {
}
