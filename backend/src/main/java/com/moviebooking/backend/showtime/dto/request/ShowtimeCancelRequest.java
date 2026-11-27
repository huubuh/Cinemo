package com.moviebooking.backend.showtime.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ShowtimeCancelRequest(
        @NotNull(message = "Version không được để trống") @PositiveOrZero Long version
) {}
