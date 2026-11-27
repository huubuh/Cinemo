package com.moviebooking.backend.cinema.service;

import com.moviebooking.backend.cinema.dto.request.CinemaRequest;
import com.moviebooking.backend.cinema.dto.request.CinemaStatusRequest;
import com.moviebooking.backend.cinema.dto.response.CinemaResponse;

import java.util.List;
import java.util.UUID;

public interface CinemaService {

    List<CinemaResponse> list(String city);

    List<CinemaResponse> listForAdmin(String city);

    CinemaResponse getById(UUID id);

    CinemaResponse getForAdmin(UUID id);

    CinemaResponse create(CinemaRequest request);

    CinemaResponse update(UUID id, CinemaRequest request);

    CinemaResponse setStatus(UUID id, CinemaStatusRequest request);
}
