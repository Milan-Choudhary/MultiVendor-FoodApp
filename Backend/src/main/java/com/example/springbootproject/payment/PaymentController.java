package com.example.springbootproject.payment;

import com.foodapp.fooddelivery.payment.dto.PaymentRequest;
import com.foodapp.fooddelivery.payment.dto.PaymentResponse;
import com.foodapp.fooddelivery.security.AppUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}/mock-charge")
    public ResponseEntity<PaymentResponse> mockCharge(@AuthenticationPrincipal AppUserPrincipal principal,
                                                      @PathVariable Long orderId,
                                                      @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.mockCharge(principal, orderId, request));
    }
}
