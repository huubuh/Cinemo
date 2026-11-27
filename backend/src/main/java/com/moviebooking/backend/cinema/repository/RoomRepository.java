package com.moviebooking.backend.cinema.repository;

import com.moviebooking.backend.cinema.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    List<Room> findAllByCinema_IdOrderByNameAsc(UUID cinemaId);

    Optional<Room> findByIdAndCinema_ActiveTrue(UUID id);

    boolean existsByCinema_IdAndNameIgnoreCase(UUID cinemaId, String name);

    boolean existsByCinema_IdAndNameIgnoreCaseAndIdNot(UUID cinemaId, String name, UUID id);
}
