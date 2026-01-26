package org.example.cinecore.service;

import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    void updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
