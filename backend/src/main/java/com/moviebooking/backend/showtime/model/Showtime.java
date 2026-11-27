package com.moviebooking.backend.showtime.model;

import com.moviebooking.backend.cinema.model.Room;
import com.moviebooking.backend.common.model.BaseEntity;
import com.moviebooking.backend.movie.model.Movie;

import com.moviebooking.backend.showtime.model.enums.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "showtimes", indexes = {
        @Index(name = "idx_showtime_room_start", columnList = "room_id, starts_at"),
        @Index(name = "idx_showtime_starts_at", columnList = "starts_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Showtime extends BaseEntity {
    // Ghi qua cột ID; quan hệ JPA chỉ để đọc/JOIN
    @Column(name = "movie_id", nullable = false)
    private UUID movieId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;
    @Column(name = "room_id", nullable = false)
    private UUID roomId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
    @Column(nullable = false)
    private Instant startsAt;
    @Column(nullable = false)
    private Instant endsAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShowtimeStatus status = ShowtimeStatus.SCHEDULED;
}
