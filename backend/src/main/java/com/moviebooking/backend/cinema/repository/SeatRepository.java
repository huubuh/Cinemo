package com.moviebooking.backend.cinema.repository;

import com.moviebooking.backend.cinema.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

    List<Seat> findAllByRoom_IdOrderByRowIndexAscColumnIndexAsc(UUID roomId);
}
