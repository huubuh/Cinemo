package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tickets", uniqueConstraints = @UniqueConstraint(columnNames = "showtime_seat_id"))
@Getter
@Setter
@NoArgsConstructor
public class Ticket extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String code;
    @Column(nullable = false, unique = true)
    private String qrToken;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ShowtimeSeat showtimeSeat;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.VALID;
    private Instant checkedInAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private User checkedInBy;
}
