package com.moviebooking.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;


@Entity
@Table(name = "booking_products", uniqueConstraints = @UniqueConstraint(columnNames = {"booking_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
public class BookingProduct extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;
    private int quantity;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;
}
