package com.moviebooking.backend.cinema.controller;

import com.moviebooking.backend.cinema.dto.response.CinemaResponse;
import com.moviebooking.backend.cinema.dto.response.RoomLayoutResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.cinema.service.CinemaService;
import com.moviebooking.backend.cinema.service.RoomService;
import com.moviebooking.backend.common.dto.response.ApiResponse;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCinemaController {

    private final CinemaService cinemaService;
    private final RoomService roomService;

    @GetMapping("/cinemas")
    public ApiResponse<List<CinemaResponse>> list(@RequestParam(required = false) @Size(max = 100) String city) {
        return ApiResponse.<List<CinemaResponse>>builder().result(cinemaService.listForAdmin(city)).build();
    }

    @GetMapping("/cinemas/{id}")
    public ApiResponse<CinemaResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<CinemaResponse>builder().result(cinemaService.getForAdmin(id)).build();
    }

    @GetMapping("/cinemas/{id}/rooms")
    public ApiResponse<List<RoomResponse>> listRooms(@PathVariable UUID id) {
        return ApiResponse.<List<RoomResponse>>builder().result(roomService.listByCinemaForAdmin(id)).build();
    }

    @GetMapping("/rooms/{id}/layout")
    public ApiResponse<RoomLayoutResponse> getLayout(@PathVariable UUID id) {
        return ApiResponse.<RoomLayoutResponse>builder().result(roomService.getLayoutForAdmin(id)).build();
    }
}
