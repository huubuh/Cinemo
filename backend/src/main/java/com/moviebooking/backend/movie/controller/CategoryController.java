package com.moviebooking.backend.movie.controller;

import com.moviebooking.backend.movie.dto.request.CategoryRequest;
import com.moviebooking.backend.common.dto.response.ApiResponse;
import com.moviebooking.backend.movie.dto.response.CategoryResponse;
import com.moviebooking.backend.movie.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> list() {
        return ApiResponse.<List<CategoryResponse>>builder().result(categoryService.list()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<CategoryResponse>builder().result(categoryService.getById(id)).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder().result(categoryService.create(request)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder().result(categoryService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
