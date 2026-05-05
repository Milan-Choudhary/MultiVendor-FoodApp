package com.foodapp.fooddelivery.order.dto;

import com.foodapp.fooddelivery.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull(message = "Order status is required")
        OrderStatus status) {
}
