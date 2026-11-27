package com.moviebooking.backend.cinema.service;

import com.moviebooking.backend.cinema.dto.request.RoomRequest;
import com.moviebooking.backend.cinema.dto.request.SeatLayoutRequest;
import com.moviebooking.backend.cinema.dto.response.RoomLayoutResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RoomService {

    List<RoomResponse> listByCinema(UUID cinemaId);

    List<RoomResponse> listByCinemaForAdmin(UUID cinemaId);

    RoomResponse getForAdmin(UUID id);

    List<RoomResponse> getAllForAdmin(Collection<UUID> ids);

    RoomLayoutResponse getLayout(UUID id);

    RoomLayoutResponse getLayoutForAdmin(UUID id);

    RoomResponse create(UUID cinemaId, RoomRequest request);

    RoomResponse update(UUID id, RoomRequest request);

    RoomLayoutResponse updateLayout(UUID id, SeatLayoutRequest request);
}
