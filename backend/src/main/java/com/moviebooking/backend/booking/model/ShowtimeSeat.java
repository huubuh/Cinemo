package com.moviebooking.backend.booking.model;

import com.moviebooking.backend.cinema.model.Seat;
import com.moviebooking.backend.common.model.BaseEntity;
import com.moviebooking.backend.showtime.model.Showtime;

import com.moviebooking.backend.booking.model.enums.ShowtimeSeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "showtime_seats", uniqueConstraints = @UniqueConstraint(columnNames = {"showtime_id", "seat_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ShowtimeSeat extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Showtime showtime;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Seat seat;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShowtimeSeatStatus status = ShowtimeSeatStatus.AVAILABLE;
    @ManyToOne(fetch = FetchType.LAZY)
    private Booking heldByBooking;
}
