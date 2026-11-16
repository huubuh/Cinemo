package com.moviebooking.backend.movie.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// Phim mới khởi chiếu trước; thêm id để thứ tự ổn định giữa các trang
final class MoviePaging {
    private static final Sort SORT = Sort.by(Sort.Order.desc("releaseDate"), Sort.Order.asc("id"));

    private MoviePaging() {
    }

    static Pageable of(int page, int size) {
        return PageRequest.of(page, size, SORT);
    }
}
