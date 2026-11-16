package com.moviebooking.backend.movie.controller;

import com.moviebooking.backend.movie.dto.request.MovieRequest;
import com.moviebooking.backend.movie.dto.request.MovieVisibilityRequest;
import com.moviebooking.backend.common.dto.response.ApiResponse;
import com.moviebooking.backend.movie.dto.response.MovieResponse;
import com.moviebooking.backend.common.dto.response.PageResponse;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import com.moviebooking.backend.movie.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> list(
            @RequestParam(required = false) @Size(max = 255) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) MovieShowingStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.list(q, categoryId, status, MoviePaging.of(page, size))).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<MovieResponse>builder().result(movieService.getById(id)).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
        return ApiResponse.<MovieResponse>builder().result(movieService.create(request)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> update(@PathVariable UUID id,
            @Validated(MovieRequest.Update.class) @RequestBody MovieRequest request) {
        return ApiResponse.<MovieResponse>builder().result(movieService.update(id, request)).build();
    }

    @PatchMapping("/{id}/visibility")
    public ApiResponse<MovieResponse> setVisibility(@PathVariable UUID id,
            @Valid @RequestBody MovieVisibilityRequest request) {
        return ApiResponse.<MovieResponse>builder().result(movieService.setVisibility(id, request)).build();
    }
}
