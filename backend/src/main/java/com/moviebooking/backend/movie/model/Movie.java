package com.moviebooking.backend.movie.model;

import com.moviebooking.backend.common.model.BaseEntity;

import jakarta.persistence.*;
import com.moviebooking.backend.movie.model.enums.AgeRating;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_movie_release_date", columnList = "release_date"),
        @Index(name = "idx_movie_end_date", columnList = "end_date")
})
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
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AgeRating ageRating;
    @Column(length = 1000)
    private String posterUrl;
    @Column(length = 1000)
    private String trailerUrl;

    @ColumnDefault("true")
    private boolean active = true;

    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(name = "movie_categories",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "category_id"}))
    private Set<Category> categories = new HashSet<>();
}
