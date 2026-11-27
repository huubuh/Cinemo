package com.moviebooking.backend.cinema.dto.request;

import com.moviebooking.backend.cinema.model.enums.SeatStatus;
import com.moviebooking.backend.cinema.model.enums.SeatType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SeatLayoutRequest(
        @Min(value = 1, message = "Số hàng phải từ 1 đến 26")
        @Max(value = SeatLayoutRequest.MAX_ROWS, message = "Số hàng phải từ 1 đến 26")
        int totalRows,
        @Min(value = 1, message = "Số cột phải từ 1 đến 50")
        @Max(value = SeatLayoutRequest.MAX_COLUMNS, message = "Số cột phải từ 1 đến 50")
        int totalColumns,
        @NotEmpty(message = "Phòng phải có ít nhất một ghế")
        @Size(max = SeatLayoutRequest.MAX_ROWS * SeatLayoutRequest.MAX_COLUMNS, message = "Quá nhiều ghế")
        List<@NotNull @Valid SeatItem> seats,
        @NotNull(message = "Version không được để trống") @PositiveOrZero Long version
) {
    public static final int MAX_ROWS = 26;
    public static final int MAX_COLUMNS = 50;

    public record SeatItem(
            @PositiveOrZero(message = "Vị trí hàng không hợp lệ") int rowIndex,
            @PositiveOrZero(message = "Vị trí cột không hợp lệ") int columnIndex,
            @NotNull(message = "Loại ghế không được để trống") SeatType type,
            SeatStatus status
    ) {}
}
