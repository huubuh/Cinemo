package com.moviebooking.backend.cinema.dto.response;

import java.util.List;
import java.util.UUID;

public record RoomLayoutResponse(UUID roomId, int totalRows, int totalColumns, Long version,
                                 List<SeatResponse> seats) {}
