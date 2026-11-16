package com.moviebooking.backend.booking.model;

import com.moviebooking.backend.auth.model.User;
import com.moviebooking.backend.common.model.BaseEntity;

import com.moviebooking.backend.booking.model.enums.GiftRedemptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "gift_redemptions")
@Getter
@Setter
@NoArgsConstructor
public class GiftRedemption extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Gift gift;
    private int quantity;
    private int pointsSpent;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GiftRedemptionStatus status = GiftRedemptionStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY)
    private User fulfilledBy;
    private Instant fulfilledAt;
}
