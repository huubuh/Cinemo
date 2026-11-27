package com.moviebooking.backend.showtime.mapper;

import com.moviebooking.backend.showtime.dto.request.TicketPriceRequest;
import com.moviebooking.backend.showtime.dto.response.SeatPriceResponse;
import com.moviebooking.backend.showtime.dto.response.TicketPriceResponse;
import com.moviebooking.backend.showtime.model.TicketPrice;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketPriceMapper {

    TicketPriceResponse toResponse(TicketPrice ticketPrice);

    SeatPriceResponse toSeatPrice(TicketPrice ticketPrice);

    TicketPrice toEntity(TicketPriceRequest request);

    void update(TicketPriceRequest request, @MappingTarget TicketPrice ticketPrice);
}
