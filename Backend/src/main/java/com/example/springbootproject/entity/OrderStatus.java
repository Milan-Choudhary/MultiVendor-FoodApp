package com.example.springbootproject.entity;

public enum OrderStatus {
    CREATED,
    ACCEPTED_BY_VENDOR,
    REJECTED_BY_VENDOR,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
