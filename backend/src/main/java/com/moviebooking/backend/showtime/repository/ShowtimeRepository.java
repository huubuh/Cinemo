package com.moviebooking.backend.showtime.repository;

import com.moviebooking.backend.showtime.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {

    // Sao chép có chủ ý quy tắc hiển thị phim/rạp/phòng để lọc trong một query
    @Query("""
            select s from Showtime s join s.movie m join s.room r join r.cinema c
            where s.startsAt >= :from and s.startsAt < :to
              and (:movieId is null or s.movieId = :movieId)
              and (:roomId is null or s.roomId = :roomId)
              and (:cinemaId is null or c.id = :cinemaId)
              and (:city is null or lower(c.city) = lower(:city))
              and (:visibleOnly = false or (s.status = com.moviebooking.backend.showtime.model.enums.ShowtimeStatus.SCHEDULED
                   and m.active = true and c.active = true
                   and r.status in (com.moviebooking.backend.cinema.model.enums.RoomStatus.AVAILABLE, com.moviebooking.backend.cinema.model.enums.RoomStatus.CLEANING)))
            order by s.startsAt, s.id
            """)
    List<Showtime> search(@Param("from") Instant from, @Param("to") Instant to,
                          @Param("movieId") UUID movieId, @Param("roomId") UUID roomId,
                          @Param("cinemaId") UUID cinemaId, @Param("city") String city,
                          @Param("visibleOnly") boolean visibleOnly);

    @Query("""
            select s from Showtime s join s.movie m join s.room r join r.cinema c
            where s.id = :id and s.status = com.moviebooking.backend.showtime.model.enums.ShowtimeStatus.SCHEDULED
                   and m.active = true and c.active = true
                   and r.status in (com.moviebooking.backend.cinema.model.enums.RoomStatus.AVAILABLE, com.moviebooking.backend.cinema.model.enums.RoomStatus.CLEANING)
            """)
    Optional<Showtime> findVisibleById(@Param("id") UUID id);

    @Query("""
            select count(s) > 0 from Showtime s
            where s.roomId = :roomId and s.status = com.moviebooking.backend.showtime.model.enums.ShowtimeStatus.SCHEDULED and s.endsAt > :now
            """)
    boolean existsUpcomingByRoom(@Param("roomId") UUID roomId, @Param("now") Instant now);

    @Query("""
            select s.startsAt from Showtime s
            where s.movieId = :movieId and s.status = com.moviebooking.backend.showtime.model.enums.ShowtimeStatus.SCHEDULED and s.endsAt > :now
            """)
    List<Instant> findUpcomingStartsByMovie(@Param("movieId") UUID movieId, @Param("now") Instant now);

    @Query("""
            select count(s) > 0 from Showtime s
            where s.roomId = :roomId and s.status = com.moviebooking.backend.showtime.model.enums.ShowtimeStatus.SCHEDULED
              and s.startsAt < :to and s.endsAt > :from
            """)
    boolean existsOverlap(@Param("roomId") UUID roomId, @Param("from") Instant from, @Param("to") Instant to);

    // Advisory lock tới hết transaction, tránh hai admin xếp suất chồng giờ cùng lúc
    @Query(value = "select count(*) from (select pg_advisory_xact_lock(hashtextextended(:roomId, 0))) l",
            nativeQuery = true)
    long lockRoomSchedule(@Param("roomId") String roomId);
}
