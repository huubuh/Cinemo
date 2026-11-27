package com.moviebooking.backend.cinema.controller;

import com.moviebooking.backend.cinema.dto.request.CinemaRequest;
import com.moviebooking.backend.cinema.dto.request.CinemaStatusRequest;
import com.moviebooking.backend.cinema.dto.request.RoomRequest;
import com.moviebooking.backend.cinema.dto.response.CinemaResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.cinema.service.CinemaService;
import com.moviebooking.backend.cinema.service.RoomService;
import com.moviebooking.backend.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;
    private final RoomService roomService;

    @GetMapping
    public ApiResponse<List<CinemaResponse>> list(@RequestParam(required = false) @Size(max = 100) String city) {
        return ApiResponse.<List<CinemaResponse>>builder().result(cinemaService.list(city)).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CinemaResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<CinemaResponse>builder().result(cinemaService.getById(id)).build();
    }

    @GetMapping("/{id}/rooms")
    public ApiResponse<List<RoomResponse>> listRooms(@PathVariable UUID id) {
        return ApiResponse.<List<RoomResponse>>builder().result(roomService.listByCinema(id)).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CinemaResponse> create(@Valid @RequestBody CinemaRequest request) {
        return ApiResponse.<CinemaResponse>builder().result(cinemaService.create(request)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CinemaResponse> update(@PathVariable UUID id,
            @Validated(CinemaRequest.Update.class) @RequestBody CinemaRequest request) {
        return ApiResponse.<CinemaResponse>builder().result(cinemaService.update(id, request)).build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<CinemaResponse> setStatus(@PathVariable UUID id,
            @Valid @RequestBody CinemaStatusRequest request) {
        return ApiResponse.<CinemaResponse>builder().result(cinemaService.setStatus(id, request)).build();
    }

    @PostMapping("/{id}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoomResponse> createRoom(@PathVariable UUID id, @Valid @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder().result(roomService.create(id, request)).build();
    }
}
