package com.moviebooking.backend.showtime.mapper;

import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.showtime.dto.response.ShowtimeResponse;
import com.moviebooking.backend.showtime.model.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShowtimeMapper {

    @Mapping(target = "id", source = "showtime.id")
    @Mapping(target = "status", source = "showtime.status")
    @Mapping(target = "version", source = "showtime.version")
    @Mapping(target = "roomName", source = "room.name")
    ShowtimeResponse toResponse(Showtime showtime, RoomResponse room);
}
