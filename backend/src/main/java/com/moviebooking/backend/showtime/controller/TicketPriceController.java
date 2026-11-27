package com.moviebooking.backend.showtime.controller;

import com.moviebooking.backend.common.dto.response.ApiResponse;
import com.moviebooking.backend.showtime.dto.request.TicketPriceRequest;
import com.moviebooking.backend.showtime.dto.response.TicketPriceResponse;
import com.moviebooking.backend.showtime.service.TicketPriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ticket-prices")
@RequiredArgsConstructor
public class TicketPriceController {

    private final TicketPriceService ticketPriceService;

    @GetMapping
    public ApiResponse<List<TicketPriceResponse>> list(@RequestParam UUID cinemaId) {
        return ApiResponse.<List<TicketPriceResponse>>builder()
                .result(ticketPriceService.listByCinema(cinemaId)).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TicketPriceResponse> create(@Valid @RequestBody TicketPriceRequest request) {
        return ApiResponse.<TicketPriceResponse>builder().result(ticketPriceService.create(request)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<TicketPriceResponse> update(@PathVariable UUID id,
            @Validated(TicketPriceRequest.Update.class) @RequestBody TicketPriceRequest request) {
        return ApiResponse.<TicketPriceResponse>builder().result(ticketPriceService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id, @RequestParam @PositiveOrZero Long version) {
        ticketPriceService.delete(id, version);
        return ApiResponse.<Void>builder().build();
    }
}
