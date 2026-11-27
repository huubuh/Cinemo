package com.moviebooking.backend.cinema.dto.event;

import java.util.UUID;

// Phát trước khi bỏ ghế/đổi loại màn hình; listener ném AppException để chặn
public record RoomChangingEvent(UUID roomId) {}
