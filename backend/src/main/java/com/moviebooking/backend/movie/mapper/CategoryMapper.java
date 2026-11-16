package com.moviebooking.backend.movie.mapper;

import com.moviebooking.backend.movie.dto.request.CategoryRequest;
import com.moviebooking.backend.movie.dto.response.CategoryResponse;
import com.moviebooking.backend.movie.model.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
    Category toEntity(CategoryRequest request);
    void update(CategoryRequest request, @MappingTarget Category category);
}
