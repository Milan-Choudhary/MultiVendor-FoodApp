package com.example.springbootproject.payment.dto;

import com.foodapp.fooddelivery.entity.PaymentStatus;
import lombok.Builder;

@Builder
public record PaymentResponse(
        Long orderId,
        PaymentStatus paymentStatus,
        String paymentReference,
        String message) {
}
