package com.moviebooking.backend.auth.mapper;

import com.moviebooking.backend.auth.dto.request.RegisterRequest;
import com.moviebooking.backend.auth.dto.response.UserResponse;
import com.moviebooking.backend.auth.model.User;
import com.moviebooking.backend.auth.security.GoogleIdentity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    User toEntity(RegisterRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "fullName", source = "name")
    User toEntity(GoogleIdentity identity);
}
