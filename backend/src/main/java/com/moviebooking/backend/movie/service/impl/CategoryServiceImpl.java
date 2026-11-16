package com.moviebooking.backend.movie.service.impl;

import com.moviebooking.backend.movie.dto.request.CategoryRequest;
import com.moviebooking.backend.movie.dto.response.CategoryResponse;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.movie.mapper.CategoryMapper;
import com.moviebooking.backend.movie.model.Category;
import com.moviebooking.backend.movie.repository.CategoryRepository;
import com.moviebooking.backend.movie.repository.MovieRepository;
import com.moviebooking.backend.movie.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MovieRepository movieRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> list() {
        return categoryRepository.findAll(Sort.by("name").ascending()).stream()
                .map(categoryMapper::toResponse).toList();
    }

    @Override
    public CategoryResponse getById(UUID id) {
        return categoryMapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        return save(categoryMapper.toEntity(request));
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = find(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        categoryMapper.update(request, category);
        return save(category);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Category category = find(id);
        if (movieRepository.existsByCategories_Id(id)) {
            throw new AppException(ErrorCode.CATEGORY_IN_USE);
        }
        try {
            categoryRepository.delete(category);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_IN_USE);
        }
    }

    private Category find(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private CategoryResponse save(Category category) {
        try {
            return categoryMapper.toResponse(categoryRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
    }
}
