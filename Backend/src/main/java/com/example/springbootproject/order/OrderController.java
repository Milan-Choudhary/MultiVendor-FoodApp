package com.foodapp.fooddelivery.order;

import com.foodapp.fooddelivery.order.dto.OrderResponse;
import com.foodapp.fooddelivery.order.dto.OrderStatusUpdateRequest;
import com.foodapp.fooddelivery.order.dto.PlaceOrderRequest;
import com.foodapp.fooddelivery.order.dto.VendorDecisionRequest;
import com.foodapp.fooddelivery.security.AppUserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/customer/orders")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal AppUserPrincipal principal,
                                                    @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(principal, request));
    }

    @GetMapping("/api/customer/orders")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(orderService.getCustomerOrders(principal.getId()));
    }

    @GetMapping("/api/vendor/orders")
    public ResponseEntity<List<OrderResponse>> getVendorOrders(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(orderService.getVendorOrders(principal.getId()));
    }

    @GetMapping("/api/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId,
                                                  @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(orderService.getOrderForPrincipal(orderId, principal));
    }

    @PatchMapping("/api/vendor/orders/{orderId}/decision")
    public ResponseEntity<OrderResponse> vendorDecision(@PathVariable Long orderId,
                                                        @AuthenticationPrincipal AppUserPrincipal principal,
                                                        @Valid @RequestBody VendorDecisionRequest request) {
        return ResponseEntity.ok(orderService.vendorDecision(orderId, principal, request));
    }

    @PatchMapping("/api/vendor/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateVendorOrderStatus(
            @PathVariable Long orderId,
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateVendorOrderStatus(orderId, principal, request));
    }
}
