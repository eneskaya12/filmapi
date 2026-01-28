package org.example.cinecore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinecore.model.dto.request.UserAdminUpdateRequest;
import org.example.cinecore.model.dto.response.GenericResponse;
import org.example.cinecore.model.dto.response.UserResponse;
import org.example.cinecore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management operations - ADMIN only")
public class UserManagementController {
    private final UserService userService;

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user details by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "User details retrieved successfully", response));
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping
    public ResponseEntity<GenericResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Users retrieved successfully", responses));
    }

    @Operation(
            summary = "Update user",
            description = "Updates user information including role. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> updateUserById(@PathVariable Long id, @RequestBody @Valid UserAdminUpdateRequest request) {
        userService.updateUserByAdmin(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "User updated successfully", null));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "User deleted successfully", null));
    }
}
