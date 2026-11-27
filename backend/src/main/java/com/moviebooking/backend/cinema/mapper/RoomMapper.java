package com.moviebooking.backend.cinema.mapper;

import com.moviebooking.backend.cinema.dto.request.RoomRequest;
import com.moviebooking.backend.cinema.dto.response.RoomLayoutResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.cinema.dto.response.SeatResponse;
import com.moviebooking.backend.cinema.model.Room;
import com.moviebooking.backend.cinema.model.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    @Mapping(target = "cinemaId", source = "cinema.id")
    RoomResponse toResponse(Room room);

    @Mapping(target = "status", defaultValue = "AVAILABLE")
    Room toEntity(RoomRequest request);

    void update(RoomRequest request, @MappingTarget Room room);

    SeatResponse toResponse(Seat seat);

    List<SeatResponse> toSeatResponses(List<Seat> seats);

    default RoomLayoutResponse toLayoutResponse(Room room, List<Seat> seats) {
        return new RoomLayoutResponse(room.getId(), room.getTotalRows(), room.getTotalColumns(),
                room.getVersion(), toSeatResponses(seats));
    }
}
