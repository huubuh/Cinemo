package com.moviebooking.backend.mapper;

import com.moviebooking.backend.dto.request.RegisterRequest;
import com.moviebooking.backend.dto.response.UserResponse;
import com.moviebooking.backend.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    // email (chuẩn hóa) và passwordHash do Service gán
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    User toEntity(RegisterRequest request);
}
