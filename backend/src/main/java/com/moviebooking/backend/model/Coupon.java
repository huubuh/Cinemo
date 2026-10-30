package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.DiscountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
public class Coupon extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    private Promotion promotion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DiscountType discountType;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal discountValue;
    @Column(precision = 14, scale = 2)
    private BigDecimal maxDiscountAmount;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal minOrderAmount;
    private Integer usageLimit;
    // Tăng trong transaction tạo booking; @Version của BaseEntity chống vượt usageLimit khi đặt đồng thời
    private int usedCount;
    private Integer perUserLimit;
    @Column(nullable = false)
    private Instant startsAt;
    @Column(nullable = false)
    private Instant endsAt;
    private boolean active = true;
}
