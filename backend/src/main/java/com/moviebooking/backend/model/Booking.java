package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.BookingChannel;
import com.moviebooking.backend.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    private User customer;
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkShift shift;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cinema cinema;
    @ManyToOne(fetch = FetchType.LAZY)
    private Showtime showtime;
    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingChannel channel = BookingChannel.ONLINE;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;
    @Column(length = 20)
    private String contactPhone;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal discountAmount;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;
    private int pointsEarned;
    private Instant expiresAt;
    private Instant confirmedAt;
}
