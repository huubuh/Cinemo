package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "showtimes", indexes = @Index(name = "idx_showtime_room_start", columnList = "room_id, starts_at"))
@Getter
@Setter
@NoArgsConstructor
public class Showtime extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Room room;
    @Column(nullable = false)
    private Instant startsAt;
    @Column(nullable = false)
    private Instant endsAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShowtimeStatus status = ShowtimeStatus.SCHEDULED;
}
