package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;


@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductType type;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;
    @Column(length = 1000)
    private String imageUrl;
    private boolean active = true;
}
