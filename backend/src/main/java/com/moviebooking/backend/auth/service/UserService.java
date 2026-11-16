package com.moviebooking.backend.auth.service;

import com.moviebooking.backend.auth.dto.response.UserResponse;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);
}
