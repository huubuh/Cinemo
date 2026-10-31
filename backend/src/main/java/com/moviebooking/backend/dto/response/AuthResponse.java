package com.moviebooking.backend.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
