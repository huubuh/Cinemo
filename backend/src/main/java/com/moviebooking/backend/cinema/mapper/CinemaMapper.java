package com.moviebooking.backend.cinema.mapper;

import com.moviebooking.backend.cinema.dto.request.CinemaRequest;
import com.moviebooking.backend.cinema.dto.response.CinemaResponse;
import com.moviebooking.backend.cinema.model.Cinema;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CinemaMapper {

    CinemaResponse toResponse(Cinema cinema);

    Cinema toEntity(CinemaRequest request);

    void update(CinemaRequest request, @MappingTarget Cinema cinema);
}
