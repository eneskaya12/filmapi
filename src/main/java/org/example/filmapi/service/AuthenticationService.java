package org.example.filmapi.service;

import org.example.filmapi.model.dto.request.UserCreateRequest;
import org.example.filmapi.model.dto.request.UserLoginRequest;
import org.example.filmapi.model.dto.response.AuthenticationResponse;
import org.example.filmapi.model.entity.User;

public interface AuthenticationService {
    AuthenticationResponse register(UserCreateRequest request);
    AuthenticationResponse login(UserLoginRequest request);
    User getAuthenticatedUser();
}
