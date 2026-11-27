package com.moviebooking.backend.cinema.dto.response;

import com.moviebooking.backend.cinema.model.enums.RoomStatus;
import com.moviebooking.backend.cinema.model.enums.ScreenType;

import java.util.UUID;

public record RoomResponse(UUID id, UUID cinemaId, String name, ScreenType screenType,
                           int totalRows, int totalColumns, RoomStatus status, Long version) {}
