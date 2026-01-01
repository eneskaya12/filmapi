package org.example.filmapi.service;

import org.example.filmapi.model.dto.request.UserUpdateRequest;
import org.example.filmapi.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    void updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
