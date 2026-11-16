package com.moviebooking.backend.movie.model.enums;

import java.time.LocalDate;

public enum MovieShowingStatus {
    NOW_SHOWING, UPCOMING, ENDED;

    public static MovieShowingStatus fromDates(LocalDate releaseDate, LocalDate endDate, LocalDate today) {
        if (releaseDate.isAfter(today)) {
            return UPCOMING;
        }
        return endDate != null && endDate.isBefore(today) ? ENDED : NOW_SHOWING;
    }
}
