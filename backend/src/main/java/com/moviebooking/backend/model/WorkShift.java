package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "work_shifts")
@Getter
@Setter
@NoArgsConstructor
public class WorkShift extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User staff;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cinema cinema;
    @Column(nullable = false)
    private Instant startsAt;
    private Instant endsAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShiftStatus status = ShiftStatus.OPEN;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal openingCash;
    @Column(precision = 14, scale = 2)
    private BigDecimal closingCash;
    @Column(precision = 14, scale = 2)
    private BigDecimal expectedCash;
    @Column(length = 5000)
    private String report;
}
