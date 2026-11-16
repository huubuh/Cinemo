package com.moviebooking.backend.movie.controller;

import com.moviebooking.backend.common.dto.response.ApiResponse;
import com.moviebooking.backend.movie.dto.response.MovieResponse;
import com.moviebooking.backend.common.dto.response.PageResponse;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import com.moviebooking.backend.movie.service.MovieService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {
    private final MovieService movieService;

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> list(
            @RequestParam(required = false) @Size(max = 255) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) MovieShowingStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.listForAdmin(q, categoryId, status, MoviePaging.of(page, size))).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<MovieResponse>builder().result(movieService.getForAdmin(id)).build();
    }
}
