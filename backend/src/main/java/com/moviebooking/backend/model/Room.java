package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.RoomStatus;
import com.moviebooking.backend.model.enums.ScreenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms", uniqueConstraints = @UniqueConstraint(columnNames = {"cinema_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cinema cinema;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScreenType screenType;
    private int totalRows;
    private int totalColumns;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomStatus status = RoomStatus.AVAILABLE;
}
