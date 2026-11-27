package com.moviebooking.backend.showtime.dto.response;

import com.moviebooking.backend.cinema.model.enums.SeatType;

import java.math.BigDecimal;

public record SeatPriceResponse(SeatType seatType, BigDecimal price) {}
