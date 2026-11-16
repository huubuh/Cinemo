package com.moviebooking.backend.auth.dto.response;

import com.moviebooking.backend.auth.model.enums.Role;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String phone,
        Role role,
        int loyaltyPoints
) {}
