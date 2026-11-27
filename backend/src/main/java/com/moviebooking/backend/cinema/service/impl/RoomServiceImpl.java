package com.moviebooking.backend.cinema.service.impl;

import com.moviebooking.backend.cinema.dto.event.RoomChangingEvent;
import com.moviebooking.backend.cinema.dto.request.RoomRequest;
import com.moviebooking.backend.cinema.dto.request.SeatLayoutRequest;
import com.moviebooking.backend.cinema.dto.request.SeatLayoutRequest.SeatItem;
import com.moviebooking.backend.cinema.dto.response.RoomLayoutResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.cinema.mapper.RoomMapper;
import com.moviebooking.backend.cinema.model.Cinema;
import com.moviebooking.backend.cinema.model.Room;
import com.moviebooking.backend.cinema.model.Seat;
import com.moviebooking.backend.cinema.model.enums.SeatStatus;
import com.moviebooking.backend.cinema.model.enums.SeatType;
import com.moviebooking.backend.cinema.repository.CinemaRepository;
import com.moviebooking.backend.cinema.repository.RoomRepository;
import com.moviebooking.backend.cinema.repository.SeatRepository;
import com.moviebooking.backend.cinema.service.RoomService;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.common.util.DbUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private static final Comparator<SeatItem> LAYOUT_ORDER =
            Comparator.comparingInt(SeatItem::rowIndex).thenComparingInt(SeatItem::columnIndex);

    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;
    private final RoomMapper roomMapper;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<RoomResponse> listByCinema(UUID cinemaId) {
        if (!cinemaRepository.existsByIdAndActiveTrue(cinemaId)) {
            throw new AppException(ErrorCode.CINEMA_NOT_FOUND);
        }
        return listRooms(cinemaId);
    }

    @Override
    public List<RoomResponse> listByCinemaForAdmin(UUID cinemaId) {
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new AppException(ErrorCode.CINEMA_NOT_FOUND);
        }
        return listRooms(cinemaId);
    }

    @Override
    public RoomResponse getForAdmin(UUID id) {
        return roomMapper.toResponse(find(id));
    }

    @Override
    public List<RoomResponse> getAllForAdmin(Collection<UUID> ids) {
        return roomRepository.findAllById(ids).stream().map(roomMapper::toResponse).toList();
    }

    @Override
    public RoomLayoutResponse getLayout(UUID id) {
        Room room = roomRepository.findByIdAndCinema_ActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        return layout(room);
    }

    @Override
    public RoomLayoutResponse getLayoutForAdmin(UUID id) {
        return layout(find(id));
    }

    @Override
    @Transactional
    public RoomResponse create(UUID cinemaId, RoomRequest request) {
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));
        if (roomRepository.existsByCinema_IdAndNameIgnoreCase(cinemaId, request.name())) {
            throw new AppException(ErrorCode.ROOM_EXISTED);
        }
        Room room = roomMapper.toEntity(request);
        room.setCinema(cinema);
        return save(room);
    }

    @Override
    @Transactional
    public RoomResponse update(UUID id, RoomRequest request) {
        Room room = find(id);
        room.checkVersion(request.version());
        if (roomRepository.existsByCinema_IdAndNameIgnoreCaseAndIdNot(room.getCinema().getId(), request.name(), id)) {
            throw new AppException(ErrorCode.ROOM_EXISTED);
        }
        if (request.screenType() != room.getScreenType()) {
            eventPublisher.publishEvent(new RoomChangingEvent(id));
        }
        roomMapper.update(request, room);
        return save(room);
    }

    // Giữ id ghế ở vị trí cũ để vé đã tham chiếu vẫn hợp lệ
    @Override
    @Transactional
    public RoomLayoutResponse updateLayout(UUID id, SeatLayoutRequest request) {
        Room room = find(id);
        room.checkVersion(request.version());
        validateLayout(request);
        // Luôn tăng version để phát hiện hai người sửa sơ đồ cùng lúc
        entityManager.lock(room, LockModeType.PESSIMISTIC_FORCE_INCREMENT);

        Map<Cell, Seat> existing = new HashMap<>();
        for (Seat seat : seatRepository.findAllByRoom_IdOrderByRowIndexAscColumnIndexAsc(id)) {
            existing.put(new Cell(seat.getRowIndex(), seat.getColumnIndex()), seat);
        }
        Set<Cell> requested = new HashSet<>();
        request.seats().forEach(item -> requested.add(new Cell(item.rowIndex(), item.columnIndex())));
        if (!requested.containsAll(existing.keySet())) {
            eventPublisher.publishEvent(new RoomChangingEvent(id));
        }
        List<Seat> seats = new ArrayList<>(request.seats().size());
        List<Seat> created = new ArrayList<>();
        for (SeatItem item : request.seats().stream().sorted(LAYOUT_ORDER).toList()) {
            Seat seat = existing.remove(new Cell(item.rowIndex(), item.columnIndex()));
            if (seat == null) {
                seat = new Seat();
                seat.setRoom(room);
                seat.setRowIndex(item.rowIndex());
                seat.setColumnIndex(item.columnIndex());
                created.add(seat);
            }
            seat.setType(item.type());
            seat.setStatus(item.status() == null ? SeatStatus.AVAILABLE : item.status());
            seats.add(seat);
        }

        // Xóa trước để nhãn cũ không trùng nhãn mới
        deleteRemovedSeats(existing.values());
        assignLabels(seats);
        seatRepository.saveAll(created);
        room.setTotalRows(request.totalRows());
        room.setTotalColumns(request.totalColumns());
        seatRepository.flush();
        return roomMapper.toLayoutResponse(room, seats);
    }

    private void deleteRemovedSeats(Collection<Seat> removed) {
        if (removed.isEmpty()) {
            return;
        }
        try {
            seatRepository.deleteAll(removed);
            seatRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.SEAT_LAYOUT_IN_USE);
        }
    }

    // seats đã sắp theo hàng, cột; hàng không có ghế không được tính nhãn
    private void assignLabels(List<Seat> seats) {
        List<Seat> renumbered = new ArrayList<>();
        int rowOrdinal = -1;
        int previousRow = -1;
        int number = 0;
        for (Seat seat : seats) {
            if (seat.getRowIndex() != previousRow) {
                previousRow = seat.getRowIndex();
                rowOrdinal++;
                number = 0;
            }
            String label = String.valueOf((char) ('A' + rowOrdinal));
            number++;
            if (seat.getId() != null && (!label.equals(seat.getRowLabel()) || number != seat.getSeatNumber())) {
                renumbered.add(seat);
            }
            seat.setRowLabel(label);
            seat.setSeatNumber(number);
        }
        // Ghi số âm tạm trước để ghế đổi số cho nhau không vi phạm unique (room, row_label, seat_number)
        if (!renumbered.isEmpty()) {
            List<String> labels = renumbered.stream().map(Seat::getRowLabel).toList();
            List<Integer> numbers = renumbered.stream().map(Seat::getSeatNumber).toList();
            for (int i = 0; i < renumbered.size(); i++) {
                renumbered.get(i).setSeatNumber(-(i + 1));
            }
            seatRepository.flush();
            for (int i = 0; i < renumbered.size(); i++) {
                renumbered.get(i).setRowLabel(labels.get(i));
                renumbered.get(i).setSeatNumber(numbers.get(i));
            }
        }
    }

    private void validateLayout(SeatLayoutRequest request) {
        Set<Cell> occupied = new HashSet<>();
        for (SeatItem item : request.seats()) {
            int width = item.type() == SeatType.COUPLE ? 2 : 1;
            if (item.rowIndex() >= request.totalRows()
                    || item.columnIndex() + width > request.totalColumns()) {
                throw new AppException(ErrorCode.INVALID_SEAT_LAYOUT);
            }
            for (int offset = 0; offset < width; offset++) {
                if (!occupied.add(new Cell(item.rowIndex(), item.columnIndex() + offset))) {
                    throw new AppException(ErrorCode.INVALID_SEAT_LAYOUT);
                }
            }
        }
    }

    private List<RoomResponse> listRooms(UUID cinemaId) {
        return roomRepository.findAllByCinema_IdOrderByNameAsc(cinemaId).stream()
                .map(roomMapper::toResponse).toList();
    }

    private RoomLayoutResponse layout(Room room) {
        return roomMapper.toLayoutResponse(room,
                seatRepository.findAllByRoom_IdOrderByRowIndexAscColumnIndexAsc(room.getId()));
    }

    private RoomResponse save(Room room) {
        try {
            return roomMapper.toResponse(roomRepository.saveAndFlush(room));
        } catch (DataIntegrityViolationException e) {
            if (DbUtils.isUniqueViolation(e)) {
                throw new AppException(ErrorCode.ROOM_EXISTED);
            }
            throw e;
        }
    }

    private Room find(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    private record Cell(int row, int column) {}
}
