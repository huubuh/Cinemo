package com.moviebooking.backend.movie.dto.event;

import java.time.LocalDate;
import java.util.UUID;

// Phát trước khi đổi thời lượng/ngày phát hành; listener (@EventListener) ném AppException để chặn
public record MovieScheduleChangingEvent(UUID movieId, boolean durationChanged,
                                         LocalDate releaseDate, LocalDate endDate) {}
