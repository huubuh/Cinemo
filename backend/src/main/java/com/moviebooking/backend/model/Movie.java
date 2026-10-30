package com.moviebooking.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
public class Movie extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(length = 5000)
    private String description;
    private int durationMinutes;
    @Column(nullable = false)
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String director;
    @Column(length = 2000)
    private String castNames;
    @Column(length = 100)
    private String language;
    @Column(length = 20)
    private String ageRating;
    @Column(length = 1000)
    private String posterUrl;
    @Column(length = 1000)
    private String trailerUrl;

    @ManyToMany
    @JoinTable(name = "movie_categories",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "category_id"}))
    private Set<Category> categories = new HashSet<>();
}
