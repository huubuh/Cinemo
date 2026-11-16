package com.moviebooking.backend.movie.repository;

import com.moviebooking.backend.movie.model.Movie;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import com.moviebooking.backend.common.util.TextUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public final class MovieSpecifications {
    private MovieSpecifications() {}

    public static Specification<Movie> filter(String q, UUID categoryId, MovieShowingStatus status,
                                               LocalDate today, boolean includeHidden) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (!includeHidden) {
                predicates.add(cb.isTrue(root.get("active")));
            }
            if (q != null && !q.isBlank()) {
                String escaped = TextUtils.removeVietnameseAccents(q.strip())
                        .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
                String accents = "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ";
                var normalizedTitle = cb.function("normalize", String.class, root.get("title"));
                var searchableTitle = cb.function("translate", String.class, cb.lower(normalizedTitle),
                        cb.literal(accents), cb.literal(TextUtils.removeVietnameseAccents(accents)));
                predicates.add(cb.like(searchableTitle, "%" + escaped + "%", '\\'));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.join("categories").get("id"), categoryId));
            }
            if (status == MovieShowingStatus.UPCOMING) {
                predicates.add(cb.greaterThan(root.get("releaseDate"), today));
            } else if (status == MovieShowingStatus.NOW_SHOWING) {
                predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), today));
                predicates.add(cb.or(cb.isNull(root.get("endDate")),
                        cb.greaterThanOrEqualTo(root.get("endDate"), today)));
            } else if (status == MovieShowingStatus.ENDED) {
                predicates.add(cb.lessThan(root.get("endDate"), today));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
