package com.moviebooking.backend.dto.response;

import com.moviebooking.backend.model.enums.Role;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String phone,
        Role role,
        int loyaltyPoints
) {}
