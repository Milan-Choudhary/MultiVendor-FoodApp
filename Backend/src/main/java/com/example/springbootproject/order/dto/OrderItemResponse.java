package com.foodapp.fooddelivery.order.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderItemResponse(
        Long id,
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal) {
}
