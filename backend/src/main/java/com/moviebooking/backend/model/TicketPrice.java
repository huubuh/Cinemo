package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.ScreenType;
import com.moviebooking.backend.model.enums.SeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.DayOfWeek;

@Entity
@Table(name = "ticket_prices")
@Getter
@Setter
@NoArgsConstructor
public class TicketPrice extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cinema cinema;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatType seatType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScreenType screenType;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DayOfWeek dayOfWeek;
    @Column(nullable = false)
    private LocalDate validFrom;
    private LocalDate validTo;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;
}
