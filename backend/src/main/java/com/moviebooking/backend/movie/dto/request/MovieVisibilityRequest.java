package com.moviebooking.backend.movie.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MovieVisibilityRequest(
        @NotNull(message = "Trạng thái hiển thị không được để trống") Boolean active,
        @NotNull(message = "Version không được để trống") @PositiveOrZero Long version
) {}
