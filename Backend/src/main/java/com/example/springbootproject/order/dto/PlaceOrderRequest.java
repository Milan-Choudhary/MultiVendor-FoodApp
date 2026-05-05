package com.foodapp.fooddelivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlaceOrderRequest(
        @NotBlank(message = "Delivery address is required")
        @Size(max = 255, message = "Delivery address must be less than 255 characters")
        String deliveryAddress) {
}
