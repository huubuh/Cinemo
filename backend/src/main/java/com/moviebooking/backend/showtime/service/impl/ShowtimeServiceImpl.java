package com.moviebooking.backend.showtime.service.impl;

import com.moviebooking.backend.cinema.dto.response.CinemaResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.cinema.model.enums.RoomStatus;
import com.moviebooking.backend.cinema.service.CinemaService;
import com.moviebooking.backend.cinema.service.RoomService;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.common.util.TextUtils;
import com.moviebooking.backend.common.util.TimeUtils;
import com.moviebooking.backend.movie.dto.response.MovieResponse;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import com.moviebooking.backend.movie.service.MovieService;
import com.moviebooking.backend.showtime.dto.request.ShowtimeCancelRequest;
import com.moviebooking.backend.showtime.dto.request.ShowtimeRequest;
import com.moviebooking.backend.showtime.dto.response.SeatPriceResponse;
import com.moviebooking.backend.showtime.dto.response.ShowtimeResponse;
import com.moviebooking.backend.showtime.mapper.ShowtimeMapper;
import com.moviebooking.backend.showtime.model.Showtime;
import com.moviebooking.backend.showtime.model.enums.ShowtimeStatus;
import com.moviebooking.backend.showtime.repository.ShowtimeRepository;
import com.moviebooking.backend.showtime.service.ShowtimeService;
import com.moviebooking.backend.showtime.service.TicketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeServiceImpl implements ShowtimeService {

    private static final Duration CLEANING_TIME = Duration.ofMinutes(15);

    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeMapper showtimeMapper;
    private final TicketPriceService ticketPriceService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final CinemaService cinemaService;

    @Override
    public List<ShowtimeResponse> list(UUID movieId, UUID cinemaId, String city, LocalDate date) {
        LocalDate day = date != null ? date : TimeUtils.today();
        // Khách chỉ thấy suất chưa bắt đầu
        Instant now = Instant.now();
        Instant from = startOf(day).isAfter(now) ? startOf(day) : now;
        return toResponses(showtimeRepository.search(from, startOf(day.plusDays(1)),
                movieId, null, cinemaId, TextUtils.stripToNull(city), true));
    }

    @Override
    public List<ShowtimeResponse> listForAdmin(UUID movieId, UUID cinemaId, UUID roomId, LocalDate date) {
        LocalDate day = date != null ? date : TimeUtils.today();
        return toResponses(showtimeRepository.search(startOf(day), startOf(day.plusDays(1)),
                movieId, roomId, cinemaId, null, false));
    }

    @Override
    public ShowtimeResponse getById(UUID id) {
        Showtime showtime = findVisible(id);
        return showtimeMapper.toResponse(showtime, roomService.getForAdmin(showtime.getRoomId()));
    }

    @Override
    public List<SeatPriceResponse> getPrices(UUID id) {
        Showtime showtime = findVisible(id);
        RoomResponse room = roomService.getForAdmin(showtime.getRoomId());
        return ticketPriceService.resolve(room.cinemaId(), room.screenType(),
                LocalDate.ofInstant(showtime.getStartsAt(), TimeUtils.ZONE));
    }

    @Override
    @Transactional
    public ShowtimeResponse create(ShowtimeRequest request) {
        MovieResponse movie = movieService.getForAdmin(request.movieId());
        RoomResponse room = roomService.getForAdmin(request.roomId());
        CinemaResponse cinema = cinemaService.getForAdmin(room.cinemaId());
        Instant startsAt = request.startsAt();
        LocalDate showDate = LocalDate.ofInstant(startsAt, TimeUtils.ZONE);
        if (!movie.active() || MovieShowingStatus.fromDates(movie.releaseDate(), movie.endDate(), showDate)
                != MovieShowingStatus.NOW_SHOWING) {
            throw new AppException(ErrorCode.MOVIE_NOT_SHOWABLE);
        }
        boolean roomUsable = room.status() == RoomStatus.AVAILABLE || room.status() == RoomStatus.CLEANING;
        if (!cinema.active() || !roomUsable || room.totalRows() == 0) {
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }
        Instant endsAt = startsAt.plus(Duration.ofMinutes(movie.durationMinutes()));

        showtimeRepository.lockRoomSchedule(room.id().toString());
        if (showtimeRepository.existsOverlap(room.id(), startsAt.minus(CLEANING_TIME), endsAt.plus(CLEANING_TIME))) {
            throw new AppException(ErrorCode.SHOWTIME_OVERLAP);
        }
        Showtime showtime = new Showtime();
        showtime.setMovieId(movie.id());
        showtime.setRoomId(room.id());
        showtime.setStartsAt(startsAt);
        showtime.setEndsAt(endsAt);
        return showtimeMapper.toResponse(showtimeRepository.saveAndFlush(showtime), room);
    }

    @Override
    @Transactional
    public ShowtimeResponse cancel(UUID id, ShowtimeCancelRequest request) {
        Showtime showtime = find(id);
        showtime.checkVersion(request.version());
        if (!showtime.getStartsAt().isAfter(Instant.now())) {
            throw new AppException(ErrorCode.SHOWTIME_STARTED);
        }
        showtime.setStatus(ShowtimeStatus.CANCELLED);
        showtimeRepository.flush();
        return showtimeMapper.toResponse(showtime, roomService.getForAdmin(showtime.getRoomId()));
    }

    private List<ShowtimeResponse> toResponses(List<Showtime> showtimes) {
        Set<UUID> roomIds = showtimes.stream().map(Showtime::getRoomId).collect(Collectors.toSet());
        Map<UUID, RoomResponse> rooms = roomService.getAllForAdmin(roomIds).stream()
                .collect(Collectors.toMap(RoomResponse::id, Function.identity()));
        return showtimes.stream().map(s -> showtimeMapper.toResponse(s, rooms.get(s.getRoomId()))).toList();
    }

    private static Instant startOf(LocalDate day) {
        return day.atStartOfDay(TimeUtils.ZONE).toInstant();
    }

    private Showtime findVisible(UUID id) {
        return showtimeRepository.findVisibleById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));
    }

    private Showtime find(UUID id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));
    }
}
