package com.moviebooking.backend.cinema.controller;

import com.moviebooking.backend.cinema.dto.request.RoomRequest;
import com.moviebooking.backend.cinema.dto.request.SeatLayoutRequest;
import com.moviebooking.backend.cinema.dto.response.RoomLayoutResponse;
import com.moviebooking.backend.cinema.dto.response.RoomResponse;
import com.moviebooking.backend.cinema.service.RoomService;
import com.moviebooking.backend.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{id}/layout")
    public ApiResponse<RoomLayoutResponse> getLayout(@PathVariable UUID id) {
        return ApiResponse.<RoomLayoutResponse>builder().result(roomService.getLayout(id)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> update(@PathVariable UUID id,
            @Validated(RoomRequest.Update.class) @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder().result(roomService.update(id, request)).build();
    }

    @PutMapping("/{id}/layout")
    public ApiResponse<RoomLayoutResponse> updateLayout(@PathVariable UUID id,
            @Valid @RequestBody SeatLayoutRequest request) {
        return ApiResponse.<RoomLayoutResponse>builder().result(roomService.updateLayout(id, request)).build();
    }
}
