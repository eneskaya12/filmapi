package org.example.filmapi.contorller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.model.dto.request.UserCreateRequest;
import org.example.filmapi.model.dto.request.UserLoginRequest;
import org.example.filmapi.model.dto.response.AuthenticationResponse;
import org.example.filmapi.model.dto.response.GenericResponse;
import org.example.filmapi.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration operations")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with USER role and returns JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<GenericResponse<AuthenticationResponse>> register(@RequestBody @Valid UserCreateRequest request) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "User registered successfully", response));
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user with email and password, returns JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public GenericResponse<AuthenticationResponse> login(@RequestBody @Valid UserLoginRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return new GenericResponse<>(true, "Login successful", response);
    }
}
