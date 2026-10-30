package com.moviebooking.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "gifts")
@Getter
@Setter
@NoArgsConstructor
public class Gift extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(length = 2000)
    private String description;
    @Column(length = 1000)
    private String imageUrl;
    private int pointsRequired;
    private int stockQuantity;
    private boolean active = true;
}
