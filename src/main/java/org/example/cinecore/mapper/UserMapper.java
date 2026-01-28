package org.example.cinecore.mapper;

import org.example.cinecore.model.dto.request.UserAdminUpdateRequest;
import org.example.cinecore.model.dto.request.UserCreateRequest;
import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.UserResponse;
import org.example.cinecore.model.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", constant = "USER")
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserAdminFromRequest(UserAdminUpdateRequest request, @MappingTarget User user);
}
