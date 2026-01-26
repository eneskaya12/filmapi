package org.example.cinecore.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.UserMapper;
import org.example.cinecore.model.dto.request.UserCreateRequest;
import org.example.cinecore.model.dto.request.UserLoginRequest;
import org.example.cinecore.model.dto.response.AuthenticationResponse;
import org.example.cinecore.model.entity.User;
import org.example.cinecore.repository.UserRepository;
import org.example.cinecore.security.CustomUserDetails;
import org.example.cinecore.security.JwtUtil;
import org.example.cinecore.service.AuthenticationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthenticationResponse register(UserCreateRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    @Override
    public AuthenticationResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    @Override
    public User getAuthenticatedUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}
