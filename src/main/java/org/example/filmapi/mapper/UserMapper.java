package org.example.filmapi.mapper;

import org.example.filmapi.model.dto.request.UserCreateRequest;
import org.example.filmapi.model.dto.request.UserUpdateRequest;
import org.example.filmapi.model.dto.response.UserResponse;
import org.example.filmapi.model.entity.User;
import org.example.filmapi.model.enums.Role;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    public User toUser(UserCreateRequest request) {
        return User.builder()
                .fullname(request.fullname())
                .email(request.email())
                .role(Role.USER)
                .build();
    }

    public UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public void updateUserFromRequest(UserUpdateRequest request, User user){
        Optional.ofNullable(request.fullname()).ifPresent(user::setFullname);
        Optional.ofNullable(request.email()).ifPresent(user::setEmail);
        Optional.ofNullable(request.role()).ifPresent(user::setRole);
    }
}
