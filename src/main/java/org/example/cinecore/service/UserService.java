package org.example.cinecore.service;

import org.example.cinecore.model.dto.request.UserAdminUpdateRequest;
import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    void updateUserByUser(Long id, UserUpdateRequest request);
    void updateUserByAdmin(Long id, UserAdminUpdateRequest request);
    void deleteUser(Long id);
}
