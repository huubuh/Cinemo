package com.moviebooking.backend.cinema.service.impl;

import com.moviebooking.backend.cinema.dto.request.CinemaRequest;
import com.moviebooking.backend.cinema.dto.request.CinemaStatusRequest;
import com.moviebooking.backend.cinema.dto.response.CinemaResponse;
import com.moviebooking.backend.cinema.mapper.CinemaMapper;
import com.moviebooking.backend.cinema.model.Cinema;
import com.moviebooking.backend.cinema.repository.CinemaRepository;
import com.moviebooking.backend.cinema.service.CinemaService;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.common.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaServiceImpl implements CinemaService {

    private static final Sort SORT = Sort.by("city", "name", "id");

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    @Override
    public List<CinemaResponse> list(String city) {
        return list(city, true);
    }

    @Override
    public List<CinemaResponse> listForAdmin(String city) {
        return list(city, false);
    }

    private List<CinemaResponse> list(String city, boolean activeOnly) {
        String filter = TextUtils.stripToNull(city);
        List<Cinema> cinemas;
        if (filter == null) {
            cinemas = activeOnly ? cinemaRepository.findAllByActiveTrue(SORT) : cinemaRepository.findAll(SORT);
        } else {
            cinemas = activeOnly ? cinemaRepository.findAllByActiveTrueAndCityIgnoreCase(filter, SORT)
                    : cinemaRepository.findAllByCityIgnoreCase(filter, SORT);
        }
        return cinemas.stream().map(cinemaMapper::toResponse).toList();
    }

    @Override
    public CinemaResponse getById(UUID id) {
        Cinema cinema = find(id);
        if (!cinema.isActive()) {
            throw new AppException(ErrorCode.CINEMA_NOT_FOUND);
        }
        return cinemaMapper.toResponse(cinema);
    }

    @Override
    public CinemaResponse getForAdmin(UUID id) {
        return cinemaMapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public CinemaResponse create(CinemaRequest request) {
        return cinemaMapper.toResponse(cinemaRepository.saveAndFlush(cinemaMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public CinemaResponse update(UUID id, CinemaRequest request) {
        Cinema cinema = find(id);
        cinema.checkVersion(request.version());
        cinemaMapper.update(request, cinema);
        cinemaRepository.flush();
        return cinemaMapper.toResponse(cinema);
    }

    @Override
    @Transactional
    public CinemaResponse setStatus(UUID id, CinemaStatusRequest request) {
        Cinema cinema = find(id);
        cinema.checkVersion(request.version());
        cinema.setActive(request.active());
        cinemaRepository.flush();
        return cinemaMapper.toResponse(cinema);
    }

    private Cinema find(UUID id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));
    }
}
