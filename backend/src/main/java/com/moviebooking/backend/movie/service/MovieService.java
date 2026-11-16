package com.moviebooking.backend.movie.service;

import com.moviebooking.backend.movie.dto.request.MovieRequest;
import com.moviebooking.backend.movie.dto.request.MovieVisibilityRequest;
import com.moviebooking.backend.movie.dto.response.MovieResponse;
import com.moviebooking.backend.common.dto.response.PageResponse;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface MovieService {
    PageResponse<MovieResponse> list(String q, UUID categoryId, MovieShowingStatus status, Pageable pageable);
    PageResponse<MovieResponse> listForAdmin(String q, UUID categoryId, MovieShowingStatus status, Pageable pageable);
    MovieResponse getById(UUID id);
    MovieResponse getForAdmin(UUID id);
    MovieResponse create(MovieRequest request);
    MovieResponse update(UUID id, MovieRequest request);
    MovieResponse setVisibility(UUID id, MovieVisibilityRequest request);
}
