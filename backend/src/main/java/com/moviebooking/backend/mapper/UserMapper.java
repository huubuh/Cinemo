package com.moviebooking.backend.mapper;

import com.moviebooking.backend.dto.request.RegisterRequest;
import com.moviebooking.backend.dto.response.UserResponse;
import com.moviebooking.backend.model.User;
import com.moviebooking.backend.security.GoogleIdentity;
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
