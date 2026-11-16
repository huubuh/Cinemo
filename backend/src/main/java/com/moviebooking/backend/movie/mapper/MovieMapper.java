package com.moviebooking.backend.movie.mapper;

import com.moviebooking.backend.movie.dto.request.MovieRequest;
import com.moviebooking.backend.movie.dto.response.MovieResponse;
import com.moviebooking.backend.movie.model.Movie;
import com.moviebooking.backend.movie.model.enums.MovieShowingStatus;
import java.time.LocalDate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = CategoryMapper.class,
        imports = MovieShowingStatus.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {
    @Mapping(target = "status", expression = "java(MovieShowingStatus.fromDates(movie.getReleaseDate(), movie.getEndDate(), today))")
    MovieResponse toResponse(Movie movie, @Context LocalDate today);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "active", ignore = true)
    Movie toEntity(MovieRequest request);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "active", ignore = true)
    void update(MovieRequest request, @MappingTarget Movie movie);
}
