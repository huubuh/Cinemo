package com.moviebooking.backend.cinema.repository;

import com.moviebooking.backend.cinema.model.Cinema;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema, UUID> {

    List<Cinema> findAllByActiveTrue(Sort sort);

    List<Cinema> findAllByActiveTrueAndCityIgnoreCase(String city, Sort sort);

    List<Cinema> findAllByCityIgnoreCase(String city, Sort sort);

    boolean existsByIdAndActiveTrue(UUID id);
}
