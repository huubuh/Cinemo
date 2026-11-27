package com.moviebooking.backend.showtime.service.impl;

import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.cinema.model.enums.SeatType;
import com.moviebooking.backend.cinema.service.CinemaService;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.showtime.dto.request.TicketPriceRequest;
import com.moviebooking.backend.showtime.dto.response.SeatPriceResponse;
import com.moviebooking.backend.showtime.dto.response.TicketPriceResponse;
import com.moviebooking.backend.showtime.mapper.TicketPriceMapper;
import com.moviebooking.backend.showtime.model.TicketPrice;
import com.moviebooking.backend.showtime.repository.TicketPriceRepository;
import com.moviebooking.backend.showtime.service.TicketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketPriceServiceImpl implements TicketPriceService {

    private static final Sort SORT = Sort.by("screenType", "seatType", "dayOfWeek", "validFrom", "id");
    // Giá theo thứ cụ thể ưu tiên hơn giá áp dụng mọi ngày, sau đó tới bảng giá mới nhất
    private static final Comparator<TicketPrice> PRIORITY = Comparator
            .comparing((TicketPrice p) -> p.getDayOfWeek() == null)
            .thenComparing(TicketPrice::getValidFrom, Comparator.reverseOrder());

    private final TicketPriceRepository ticketPriceRepository;
    private final TicketPriceMapper ticketPriceMapper;
    private final CinemaService cinemaService;

    @Override
    public List<TicketPriceResponse> listByCinema(UUID cinemaId) {
        cinemaService.getForAdmin(cinemaId);
        return ticketPriceRepository.findAllByCinemaId(cinemaId, SORT).stream()
                .map(ticketPriceMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public TicketPriceResponse create(TicketPriceRequest request) {
        validate(request, null);
        return ticketPriceMapper.toResponse(ticketPriceRepository.saveAndFlush(ticketPriceMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public TicketPriceResponse update(UUID id, TicketPriceRequest request) {
        TicketPrice ticketPrice = find(id);
        ticketPrice.checkVersion(request.version());
        validate(request, id);
        ticketPriceMapper.update(request, ticketPrice);
        ticketPriceRepository.flush();
        return ticketPriceMapper.toResponse(ticketPrice);
    }

    @Override
    @Transactional
    public void delete(UUID id, Long version) {
        TicketPrice ticketPrice = find(id);
        ticketPrice.checkVersion(version);
        ticketPriceRepository.delete(ticketPrice);
    }

    @Override
    public List<SeatPriceResponse> resolve(UUID cinemaId, ScreenType screenType, LocalDate date) {
        Map<SeatType, TicketPrice> best = ticketPriceRepository
                .findApplicable(cinemaId, screenType, date, date.getDayOfWeek()).stream()
                .collect(Collectors.toMap(TicketPrice::getSeatType, Function.identity(),
                        BinaryOperator.minBy(PRIORITY), () -> new EnumMap<>(SeatType.class)));
        return best.values().stream().map(ticketPriceMapper::toSeatPrice).toList();
    }

    private void validate(TicketPriceRequest request, UUID currentId) {
        if (request.validTo() != null && request.validTo().isBefore(request.validFrom())) {
            throw new AppException(ErrorCode.INVALID_TICKET_PRICE_DATES);
        }
        cinemaService.getForAdmin(request.cinemaId());
        ticketPriceRepository.lockPriceGroup(
                "ticket-price:" + request.cinemaId() + ":" + request.seatType() + ":" + request.screenType());
        boolean overlap = ticketPriceRepository
                .findAllByCinemaIdAndSeatTypeAndScreenType(request.cinemaId(), request.seatType(), request.screenType())
                .stream()
                .filter(p -> !p.getId().equals(currentId) && p.getDayOfWeek() == request.dayOfWeek())
                .anyMatch(p -> overlaps(p.getValidFrom(), p.getValidTo(), request.validFrom(), request.validTo()));
        if (overlap) {
            throw new AppException(ErrorCode.TICKET_PRICE_OVERLAP);
        }
    }

    private static boolean overlaps(LocalDate from1, LocalDate to1, LocalDate from2, LocalDate to2) {
        return (to2 == null || !from1.isAfter(to2)) && (to1 == null || !from2.isAfter(to1));
    }

    private TicketPrice find(UUID id) {
        return ticketPriceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_PRICE_NOT_FOUND));
    }
}
