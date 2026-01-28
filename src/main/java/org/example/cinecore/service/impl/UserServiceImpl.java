package org.example.cinecore.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.UserMapper;
import org.example.cinecore.model.dto.request.UserAdminUpdateRequest;
import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.UserResponse;
import org.example.cinecore.model.entity.User;
import org.example.cinecore.repository.UserRepository;
import org.example.cinecore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private  final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserById(Long id) {
        User user = getUser(id);
        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public void updateUserByUser(Long id, UserUpdateRequest request) {
        User user = getUser(id);
        userMapper.updateUserFromRequest(request, user);
        Optional.ofNullable(request.password())
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);

        userRepository.save(user);
    }

    @Override
    public void updateUserByAdmin(Long id, UserAdminUpdateRequest request) {
        User user = getUser(id);
        userMapper.updateUserAdminFromRequest(request, user);
        Optional.ofNullable(request.password())
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    private User getUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
