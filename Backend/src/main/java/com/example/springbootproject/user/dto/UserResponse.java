package com.foodapp.fooddelivery.user.dto;

import com.foodapp.fooddelivery.entity.Role;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        boolean online) {
}
