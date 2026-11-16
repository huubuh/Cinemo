package com.moviebooking.backend.movie.dto.response;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import com.moviebooking.backend.movie.model.enums.AgeRating;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;

public record MovieResponse(
        UUID id, String title, String description, int durationMinutes,
        LocalDate releaseDate, LocalDate endDate, String director, String castNames,
        String language, AgeRating ageRating, String posterUrl, String trailerUrl,
        Set<CategoryResponse> categories, MovieShowingStatus status, boolean active, Long version
) {}
