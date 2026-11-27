package com.moviebooking.backend.showtime.dto.response;

import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.showtime.model.enums.ShowtimeStatus;

import java.time.Instant;
import java.util.UUID;

public record ShowtimeResponse(UUID id, UUID movieId, UUID roomId, String roomName, UUID cinemaId,
                               ScreenType screenType, Instant startsAt, Instant endsAt,
                               ShowtimeStatus status, Long version) {}
