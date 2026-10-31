package com.moviebooking.backend.service;

import com.moviebooking.backend.dto.response.UserResponse;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);
}
