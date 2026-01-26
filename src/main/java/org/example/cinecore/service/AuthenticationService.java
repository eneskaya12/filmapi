package org.example.cinecore.service;

import org.example.cinecore.model.dto.request.UserCreateRequest;
import org.example.cinecore.model.dto.request.UserLoginRequest;
import org.example.cinecore.model.dto.response.AuthenticationResponse;
import org.example.cinecore.model.entity.User;

public interface AuthenticationService {
    AuthenticationResponse register(UserCreateRequest request);
    AuthenticationResponse login(UserLoginRequest request);
    User getAuthenticatedUser();
}
