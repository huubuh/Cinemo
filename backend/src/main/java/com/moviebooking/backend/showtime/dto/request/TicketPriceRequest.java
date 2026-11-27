package com.moviebooking.backend.showtime.dto.request;

import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.cinema.model.enums.SeatType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.groups.Default;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

public record TicketPriceRequest(
        @NotNull(message = "Rạp không được để trống") UUID cinemaId,
        @NotNull(message = "Loại ghế không được để trống") SeatType seatType,
        @NotNull(message = "Loại màn hình không được để trống") ScreenType screenType,
        // Bỏ trống nghĩa là áp dụng mọi ngày; giá theo thứ cụ thể được ưu tiên hơn
        DayOfWeek dayOfWeek,
        @NotNull(message = "Ngày bắt đầu áp dụng không được để trống") LocalDate validFrom,
        LocalDate validTo,
        @NotNull(message = "Giá vé không được để trống")
        @Positive(message = "Giá vé phải lớn hơn 0")
        @Digits(integer = 12, fraction = 2, message = "Giá vé không hợp lệ") BigDecimal price,
        @NotNull(groups = Update.class, message = "Version không được để trống khi sửa giá vé")
        @PositiveOrZero Long version
) {
    public interface Update extends Default {}
}
