package com.moviebooking.backend.showtime.service;

import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.showtime.dto.request.TicketPriceRequest;
import com.moviebooking.backend.showtime.dto.response.SeatPriceResponse;
import com.moviebooking.backend.showtime.dto.response.TicketPriceResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TicketPriceService {

    List<TicketPriceResponse> listByCinema(UUID cinemaId);

    TicketPriceResponse create(TicketPriceRequest request);

    TicketPriceResponse update(UUID id, TicketPriceRequest request);

    void delete(UUID id, Long version);

    // Loại ghế chưa có giá thì không có trong kết quả
    List<SeatPriceResponse> resolve(UUID cinemaId, ScreenType screenType, LocalDate date);
}
