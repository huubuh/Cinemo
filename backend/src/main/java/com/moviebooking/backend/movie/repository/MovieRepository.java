package com.moviebooking.backend.movie.repository;

import com.moviebooking.backend.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID>, JpaSpecificationExecutor<Movie> {
    boolean existsByCategories_Id(UUID categoryId);
}
