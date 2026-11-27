package com.moviebooking.backend.cinema.dto.response;

import com.moviebooking.backend.cinema.model.enums.SeatStatus;
import com.moviebooking.backend.cinema.model.enums.SeatType;

import java.util.UUID;

public record SeatResponse(UUID id, String rowLabel, int seatNumber, int rowIndex, int columnIndex,
                           SeatType type, SeatStatus status) {}
