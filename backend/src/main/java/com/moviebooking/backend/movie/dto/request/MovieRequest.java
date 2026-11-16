package com.moviebooking.backend.movie.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import com.moviebooking.backend.movie.model.enums.AgeRating;
import com.moviebooking.backend.common.util.TextUtils;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record MovieRequest(
        @NotBlank(message = "Tên phim không được để trống") @Size(max = 255) String title,
        @Size(max = 5000) String description,
        @Positive(message = "Thời lượng phim phải lớn hơn 0") @Max(600) int durationMinutes,
        @NotNull(message = "Ngày khởi chiếu không được để trống") LocalDate releaseDate,
        LocalDate endDate,
        @Size(max = 255) String director,
        @Size(max = 2000) String castNames,
        @Size(max = 100) String language,
        AgeRating ageRating,
        @URL(protocol = "https", message = "URL poster phải dùng HTTPS") @Size(max = 1000) String posterUrl,
        @URL(protocol = "https", message = "URL trailer phải dùng HTTPS") @Size(max = 1000) String trailerUrl,
        @NotNull(message = "Danh sách thể loại không được null")
        @Size(max = 100) Set<@NotNull UUID> categoryIds,
        @NotNull(groups = Update.class, message = "Version không được để trống khi sửa phim")
        @PositiveOrZero Long version
) {
    public interface Update extends Default {}

    public MovieRequest {
        title = TextUtils.stripToNull(title);
        description = TextUtils.stripToNull(description);
        director = TextUtils.stripToNull(director);
        castNames = TextUtils.stripToNull(castNames);
        language = TextUtils.stripToNull(language);
        posterUrl = TextUtils.stripToNull(posterUrl);
        trailerUrl = TextUtils.stripToNull(trailerUrl);
    }
}
