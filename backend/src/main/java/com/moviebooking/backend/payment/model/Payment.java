package com.moviebooking.backend.payment.model;

import com.moviebooking.backend.booking.model.Booking;
import com.moviebooking.backend.common.model.BaseEntity;

import com.moviebooking.backend.payment.model.enums.PaymentMethod;
import com.moviebooking.backend.payment.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Booking booking;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;
    @Column(length = 100)
    private String provider;
    @Column(unique = true)
    private String transactionReference;
    private Instant paidAt;
}
