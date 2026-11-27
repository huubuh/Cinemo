package com.moviebooking.backend.showtime.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ShowtimeRequest(
        @NotNull(message = "Phim không được để trống") UUID movieId,
        @NotNull(message = "Phòng chiếu không được để trống") UUID roomId,
        @NotNull(message = "Giờ chiếu không được để trống")
        @Future(message = "Giờ chiếu phải ở tương lai") Instant startsAt
) {}
