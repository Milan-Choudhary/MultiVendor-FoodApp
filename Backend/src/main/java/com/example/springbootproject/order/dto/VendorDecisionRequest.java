package com.foodapp.fooddelivery.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VendorDecisionRequest(
        @NotNull(message = "Approved flag is required")
        Boolean approved,
        @Size(max = 255, message = "Rejection reason must be less than 255 characters")
        String rejectionReason) {
}
