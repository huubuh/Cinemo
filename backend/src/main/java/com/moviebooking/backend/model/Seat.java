package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.SeatStatus;
import com.moviebooking.backend.model.enums.SeatType;
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
    // Vị trí trên lưới của phòng (0-based); ô không có ghế là lối đi. Ghế COUPLE chiếm thêm ô columnIndex + 1
    private int rowIndex;
    private int columnIndex;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatType type = SeatType.STANDARD;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatStatus status = SeatStatus.AVAILABLE;
}
