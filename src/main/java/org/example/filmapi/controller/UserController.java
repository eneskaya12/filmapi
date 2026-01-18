package org.example.filmapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.mapper.UserMapper;
import org.example.filmapi.model.dto.request.UserUpdateRequest;
import org.example.filmapi.model.dto.response.GenericResponse;
import org.example.filmapi.model.dto.response.UserResponse;
import org.example.filmapi.model.entity.User;
import org.example.filmapi.service.AuthenticationService;
import org.example.filmapi.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Current user's profile operations")
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Get my profile",
            description = "Returns the current authenticated user's profile. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public GenericResponse<UserResponse> getMyProfile() {
        User user = authenticationService.getAuthenticatedUser();
        UserResponse response = userMapper.toUserResponse(user);
        return new GenericResponse<>(true, "Profile retrieved successfully", response);
    }

    @Operation(
            summary = "Update my profile",
            description = "Updates the current authenticated user's profile. Requires authentication. Note: Role cannot be changed by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping
    public GenericResponse<Void> updateMyProfile(@RequestBody @Valid UserUpdateRequest request) {
        User user = authenticationService.getAuthenticatedUser();

        UserUpdateRequest safeRequest = new UserUpdateRequest(
                request.fullname(),
                request.email(),
                request.password(),
                null
        );
        userService.updateUser(user.getId(), safeRequest);
        return new GenericResponse<>(true, "Profile updated successfully", null);
    }
}
