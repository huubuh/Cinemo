package com.moviebooking.backend.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.moviebooking.backend.common.util.TextUtils;

public record CategoryRequest(
        @NotBlank(message = "Tên thể loại không được để trống")
        @Size(max = 100, message = "Tên thể loại tối đa 100 ký tự") String name,
        @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự") String description
) {
    public CategoryRequest {
        name = TextUtils.stripToNull(name);
        description = TextUtils.stripToNull(description);
    }
}
