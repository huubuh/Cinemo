package com.moviebooking.backend.cinema.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CinemaStatusRequest(
        @NotNull(message = "Trạng thái hoạt động không được để trống") Boolean active,
        @NotNull(message = "Version không được để trống") @PositiveOrZero Long version
) {}
