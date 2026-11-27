package com.moviebooking.backend.showtime.service.impl;

import com.moviebooking.backend.cinema.dto.event.RoomChangingEvent;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.common.util.TimeUtils;
import com.moviebooking.backend.movie.dto.event.MovieScheduleChangingEvent;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import com.moviebooking.backend.showtime.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

// Listener chạy trong transaction của bên phát event nên ném AppException sẽ rollback thay đổi
@Component
@RequiredArgsConstructor
public class ShowtimeScheduleGuard {

    private final ShowtimeRepository showtimeRepository;

    @EventListener
    public void onRoomChanging(RoomChangingEvent event) {
        if (showtimeRepository.existsUpcomingByRoom(event.roomId(), Instant.now())) {
            throw new AppException(ErrorCode.ROOM_HAS_UPCOMING_SHOWTIMES);
        }
    }

    // Đổi thời lượng làm sai giờ kết thúc đã lưu; đổi ngày phát hành chỉ chặn khi có suất rơi ra ngoài khoảng mới
    @EventListener
    public void onMovieScheduleChanging(MovieScheduleChangingEvent event) {
        var starts = showtimeRepository.findUpcomingStartsByMovie(event.movieId(), Instant.now());
        boolean conflict = event.durationChanged()
                ? !starts.isEmpty()
                : starts.stream().map(start -> LocalDate.ofInstant(start, TimeUtils.ZONE))
                        .anyMatch(day -> MovieShowingStatus.fromDates(event.releaseDate(), event.endDate(), day)
                                != MovieShowingStatus.NOW_SHOWING);
        if (conflict) {
            throw new AppException(ErrorCode.MOVIE_HAS_UPCOMING_SHOWTIMES);
        }
    }
}
