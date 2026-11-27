package com.moviebooking.backend.cinema.dto.request;

import com.moviebooking.backend.cinema.model.enums.RoomStatus;
import com.moviebooking.backend.cinema.model.enums.ScreenType;
import com.moviebooking.backend.common.util.TextUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record RoomRequest(
        @NotBlank(message = "Tên phòng không được để trống")
        @Size(max = 100, message = "Tên phòng tối đa 100 ký tự") String name,
        @NotNull(message = "Loại màn hình không được để trống") ScreenType screenType,
        @NotNull(groups = Update.class, message = "Trạng thái phòng không được để trống") RoomStatus status,
        @NotNull(groups = Update.class, message = "Version không được để trống khi sửa phòng")
        @PositiveOrZero Long version
) {
    public interface Update extends Default {}

    public RoomRequest {
        name = TextUtils.stripToNull(name);
    }
}
