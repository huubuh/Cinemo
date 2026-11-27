package com.moviebooking.backend.showtime.repository;

import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.cinema.model.enums.SeatType;
import com.moviebooking.backend.showtime.model.TicketPrice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TicketPriceRepository extends JpaRepository<TicketPrice, UUID> {

    List<TicketPrice> findAllByCinemaId(UUID cinemaId, Sort sort);

    List<TicketPrice> findAllByCinemaIdAndSeatTypeAndScreenType(UUID cinemaId, SeatType seatType, ScreenType screenType);

    // Advisory lock tới hết transaction, tránh tạo trùng bảng giá cùng lúc
    @Query(value = "select count(*) from (select pg_advisory_xact_lock(hashtextextended(:groupKey, 0))) l",
            nativeQuery = true)
    long lockPriceGroup(@Param("groupKey") String groupKey);

    @Query("""
            select p from TicketPrice p
            where p.cinemaId = :cinemaId and p.screenType = :screenType
              and p.validFrom <= :date and (p.validTo is null or p.validTo >= :date)
              and (p.dayOfWeek is null or p.dayOfWeek = :dayOfWeek)
            """)
    List<TicketPrice> findApplicable(@Param("cinemaId") UUID cinemaId, @Param("screenType") ScreenType screenType,
                                     @Param("date") LocalDate date, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}
