package com.moviebooking.backend.movie.service.impl;

import com.moviebooking.backend.movie.dto.request.MovieRequest;
import com.moviebooking.backend.movie.dto.request.MovieVisibilityRequest;
import com.moviebooking.backend.movie.dto.response.MovieResponse;
import com.moviebooking.backend.common.dto.response.PageResponse;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.movie.mapper.MovieMapper;
import com.moviebooking.backend.movie.model.Category;
import com.moviebooking.backend.movie.model.Movie;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import com.moviebooking.backend.movie.repository.CategoryRepository;
import com.moviebooking.backend.movie.repository.MovieRepository;
import com.moviebooking.backend.movie.repository.MovieSpecifications;
import com.moviebooking.backend.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final MovieMapper movieMapper;

    @Override
    public PageResponse<MovieResponse> list(String q, UUID categoryId, MovieShowingStatus status, Pageable pageable) {
        return list(q, categoryId, status, pageable, false);
    }

    @Override
    public PageResponse<MovieResponse> listForAdmin(String q, UUID categoryId, MovieShowingStatus status, Pageable pageable) {
        return list(q, categoryId, status, pageable, true);
    }

    private PageResponse<MovieResponse> list(String q, UUID categoryId, MovieShowingStatus status,
                                             Pageable pageable, boolean includeHidden) {
        LocalDate today = today();
        var page = movieRepository.findAll(MovieSpecifications.filter(q, categoryId, status,
                today, includeHidden), pageable);
        return new PageResponse<>(page.getContent().stream().map(movie -> movieMapper.toResponse(movie, today)).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public MovieResponse getById(UUID id) {
        Movie movie = find(id);
        if (!movie.isActive()) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }
        return movieMapper.toResponse(movie, today());
    }

    @Override
    public MovieResponse getForAdmin(UUID id) {
        return movieMapper.toResponse(find(id), today());
    }

    @Override
    @Transactional
    public MovieResponse create(MovieRequest request) {
        validateDates(request);
        var categories = resolveCategories(request.categoryIds());
        Movie movie = movieMapper.toEntity(request);
        movie.setCategories(new HashSet<>(categories));
        return movieMapper.toResponse(movieRepository.save(movie), today());
    }

    @Override
    @Transactional
    public MovieResponse update(UUID id, MovieRequest request) {
        Movie movie = find(id);
        validateVersion(movie, request.version());
        validateDates(request);
        var categories = resolveCategories(request.categoryIds());
        movieMapper.update(request, movie);
        movie.getCategories().clear();
        movie.getCategories().addAll(categories);
        movieRepository.flush();
        return movieMapper.toResponse(movie, today());
    }

    @Override
    @Transactional
    public MovieResponse setVisibility(UUID id, MovieVisibilityRequest request) {
        Movie movie = find(id);
        validateVersion(movie, request.version());
        movie.setActive(request.active());
        movieRepository.flush();
        return movieMapper.toResponse(movie, today());
    }

    private void validateVersion(Movie movie, Long version) {
        if (version == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (!version.equals(movie.getVersion())) {
            throw new AppException(ErrorCode.CONCURRENT_UPDATE);
        }
    }

    private LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    private Movie find(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
    }

    private Set<Category> resolveCategories(Set<UUID> ids) {
        if (ids.isEmpty()) {
            return Set.of();
        }
        var categories = categoryRepository.findAllById(ids);
        if (categories.size() != ids.size()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return new HashSet<>(categories);
    }

    private void validateDates(MovieRequest request) {
        if (request.endDate() != null && request.endDate().isBefore(request.releaseDate())) {
            throw new AppException(ErrorCode.INVALID_MOVIE_DATES);
        }
    }
}
