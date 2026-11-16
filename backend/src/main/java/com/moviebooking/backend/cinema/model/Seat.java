package com.moviebooking.backend.cinema.model;

import com.moviebooking.backend.common.model.BaseEntity;

import com.moviebooking.backend.cinema.model.enums.SeatStatus;
import com.moviebooking.backend.cinema.model.enums.SeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "row_label", "seat_number"}),
        @UniqueConstraint(columnNames = {"room_id", "row_index", "column_index"})
})
@Getter
@Setter
@NoArgsConstructor
public class Seat extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Room room;
    @Column(nullable = false, length = 10)
    private String rowLabel;
    private int seatNumber;
    // Vị trí ghế trong sơ đồ phòng, đếm từ 0; ô trống là lối đi, ghế đôi chiếm 2 ô
    private int rowIndex;
    private int columnIndex;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatType type = SeatType.STANDARD;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatStatus status = SeatStatus.AVAILABLE;
}
