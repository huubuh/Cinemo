package com.moviebooking.backend.cinema.dto.response;

import java.util.UUID;

public record CinemaResponse(UUID id, String name, String address, String city, String phone,
                             boolean active, Long version) {}
