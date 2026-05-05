package com.foodapp.fooddelivery.auth.dto;

import com.foodapp.fooddelivery.user.dto.UserResponse;
import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user) {
}
