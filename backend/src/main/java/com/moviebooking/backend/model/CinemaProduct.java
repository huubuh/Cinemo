package com.moviebooking.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cinema_products", uniqueConstraints = @UniqueConstraint(columnNames = {"cinema_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CinemaProduct extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cinema cinema;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;
    private int stockQuantity;
    private boolean active = true;
}
