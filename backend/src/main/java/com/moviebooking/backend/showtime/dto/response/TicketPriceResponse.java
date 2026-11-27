package com.moviebooking.backend.showtime.dto.response;

import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.cinema.model.enums.SeatType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

public record TicketPriceResponse(UUID id, UUID cinemaId, SeatType seatType, ScreenType screenType,
                                  DayOfWeek dayOfWeek, LocalDate validFrom, LocalDate validTo,
                                  BigDecimal price, Long version) {}
