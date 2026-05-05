package com.foodapp.fooddelivery.order.dto;

import com.foodapp.fooddelivery.entity.DeliveryStatus;
import lombok.Builder;

@Builder
public record DeliverySummaryResponse(
        Long id,
        Long deliveryPartnerId,
        String deliveryPartnerName,
        DeliveryStatus status) {
}
