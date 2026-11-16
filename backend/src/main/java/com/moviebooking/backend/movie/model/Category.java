package com.moviebooking.backend.movie.model;

import com.moviebooking.backend.common.model.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(length = 1000)
    private String description;
}
