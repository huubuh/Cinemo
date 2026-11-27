package com.moviebooking.backend.showtime.controller;

import com.moviebooking.backend.common.dto.response.ApiResponse;
import com.moviebooking.backend.showtime.dto.request.ShowtimeCancelRequest;
import com.moviebooking.backend.showtime.dto.request.ShowtimeRequest;
import com.moviebooking.backend.showtime.dto.response.SeatPriceResponse;
import com.moviebooking.backend.showtime.dto.response.ShowtimeResponse;
import com.moviebooking.backend.showtime.service.ShowtimeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public ApiResponse<List<ShowtimeResponse>> list(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) @Size(max = 100) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.list(movieId, cinemaId, city, date)).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ShowtimeResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<ShowtimeResponse>builder().result(showtimeService.getById(id)).build();
    }

    @GetMapping("/{id}/prices")
    public ApiResponse<List<SeatPriceResponse>> getPrices(@PathVariable UUID id) {
        return ApiResponse.<List<SeatPriceResponse>>builder().result(showtimeService.getPrices(id)).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ShowtimeResponse> create(@Valid @RequestBody ShowtimeRequest request) {
        return ApiResponse.<ShowtimeResponse>builder().result(showtimeService.create(request)).build();
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<ShowtimeResponse> cancel(@PathVariable UUID id, @Valid @RequestBody ShowtimeCancelRequest request) {
        return ApiResponse.<ShowtimeResponse>builder().result(showtimeService.cancel(id, request)).build();
    }
}
