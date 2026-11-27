package com.moviebooking.backend.showtime.service;

import com.moviebooking.backend.showtime.dto.request.ShowtimeCancelRequest;
import com.moviebooking.backend.showtime.dto.request.ShowtimeRequest;
import com.moviebooking.backend.showtime.dto.response.SeatPriceResponse;
import com.moviebooking.backend.showtime.dto.response.ShowtimeResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShowtimeService {

    List<ShowtimeResponse> list(UUID movieId, UUID cinemaId, String city, LocalDate date);

    List<ShowtimeResponse> listForAdmin(UUID movieId, UUID cinemaId, UUID roomId, LocalDate date);

    ShowtimeResponse getById(UUID id);

    List<SeatPriceResponse> getPrices(UUID id);

    ShowtimeResponse create(ShowtimeRequest request);

    ShowtimeResponse cancel(UUID id, ShowtimeCancelRequest request);
}
