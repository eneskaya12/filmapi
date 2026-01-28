package org.example.cinecore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinecore.mapper.UserMapper;
import org.example.cinecore.model.dto.request.UserAdminUpdateRequest;
import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.GenericResponse;
import org.example.cinecore.model.dto.response.UserResponse;
import org.example.cinecore.model.entity.User;
import org.example.cinecore.service.AuthenticationService;
import org.example.cinecore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GenericResponse<UserResponse>> getMyProfile() {
        User user = authenticationService.getAuthenticatedUser();
        UserResponse response = userMapper.toUserResponse(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Profile retrieved successfully", response));
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
    public ResponseEntity<GenericResponse<Void>> updateMyProfile(@RequestBody @Valid UserUpdateRequest request) {
        User user = authenticationService.getAuthenticatedUser();

        userService.updateUserByUser(user.getId(), request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Profile updated successfully", null));
    }
}
