package com.moviebooking.backend.cinema.dto.request;

import com.moviebooking.backend.common.util.TextUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record CinemaRequest(
        @NotBlank(message = "Tên rạp không được để trống")
        @Size(max = 255, message = "Tên rạp tối đa 255 ký tự") String name,
        @NotBlank(message = "Địa chỉ không được để trống")
        @Size(max = 1000, message = "Địa chỉ tối đa 1000 ký tự") String address,
        @NotBlank(message = "Thành phố không được để trống")
        @Size(max = 100, message = "Thành phố tối đa 100 ký tự") String city,
        @Pattern(regexp = "\\+?[0-9]{8,15}", message = "Số điện thoại không hợp lệ") String phone,
        @NotNull(groups = Update.class, message = "Version không được để trống khi sửa rạp")
        @PositiveOrZero Long version
) {
    public interface Update extends Default {}

    public CinemaRequest {
        name = TextUtils.stripToNull(name);
        address = TextUtils.stripToNull(address);
        city = TextUtils.stripToNull(city);
        phone = TextUtils.stripToNull(phone);
    }
}
