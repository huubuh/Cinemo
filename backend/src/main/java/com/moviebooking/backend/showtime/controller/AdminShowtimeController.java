package com.moviebooking.backend.showtime.controller;

import com.moviebooking.backend.common.dto.response.ApiResponse;
import com.moviebooking.backend.showtime.dto.response.ShowtimeResponse;
import com.moviebooking.backend.showtime.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class AdminShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public ApiResponse<List<ShowtimeResponse>> list(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.listForAdmin(movieId, cinemaId, roomId, date)).build();
    }
}
