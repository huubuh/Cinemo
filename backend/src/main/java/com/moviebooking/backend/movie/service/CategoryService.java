package com.moviebooking.backend.movie.service;

import com.moviebooking.backend.movie.dto.request.CategoryRequest;
import com.moviebooking.backend.movie.dto.response.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> list();
    CategoryResponse getById(UUID id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(UUID id, CategoryRequest request);
    void delete(UUID id);
}
