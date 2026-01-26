package org.example.cinecore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cinecore.config.TestSecurityConfig;
import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.UserResponse;
import org.example.cinecore.model.enums.Role;
import org.example.cinecore.security.JwtAuthenticationFilter;
import org.example.cinecore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserManagementController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@Import(TestSecurityConfig.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .id(1L)
                .fullname("John Doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();

        updateRequest = new UserUpdateRequest("Jane Doe", "jane@example.com", "newPassword123", Role.ADMIN);
    }

    @Nested
    @DisplayName("GET /api/users/{id} - ADMIN only")
    class GetUserByIdTests {

        @Test
        @DisplayName("ADMIN should get user by id")
        @WithMockUser(roles = "ADMIN")
        void getUserById_WithAdminRole_ShouldReturn200() throws Exception {
            when(userService.getUserById(1L)).thenReturn(userResponse);

            mockMvc.perform(get("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("User details retrieved successfully"))
                    .andExpect(jsonPath("$.payload.fullname").value("John Doe"));

            verify(userService, times(1)).getUserById(1L);
        }

        @Test
        @DisplayName("USER should get 403")
        @WithMockUser(roles = "USER")
        void getUserById_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getUserById(any());
        }

        @Test
        @DisplayName("Unauthenticated should get 403")
        void getUserById_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getUserById(any());
        }
    }

    @Nested
    @DisplayName("GET /api/users - ADMIN only")
    class GetAllUsersTests {

        @Test
        @DisplayName("ADMIN should get all users")
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_WithAdminRole_ShouldReturn200() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(userResponse));

            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                    .andExpect(jsonPath("$.payload[0].fullname").value("John Doe"));

            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("USER should get 403")
        @WithMockUser(roles = "USER")
        void getAllUsers_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getAllUsers();
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/{id} - ADMIN only")
    class UpdateUserTests {

        @Test
        @DisplayName("ADMIN should update user")
        @WithMockUser(roles = "ADMIN")
        void updateUser_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(userService).updateUser(eq(1L), any(UserUpdateRequest.class));

            mockMvc.perform(patch("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("User updated successfully"));

            verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateRequest.class));
        }

        @Test
        @DisplayName("USER should get 403")
        @WithMockUser(roles = "USER")
        void updateUser_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(patch("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());

            verify(userService, never()).updateUser(any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id} - ADMIN only")
    class DeleteUserTests {

        @Test
        @DisplayName("ADMIN should delete user")
        @WithMockUser(roles = "ADMIN")
        void deleteUser_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("User deleted successfully"));

            verify(userService, times(1)).deleteUser(1L);
        }

        @Test
        @DisplayName("USER should get 403")
        @WithMockUser(roles = "USER")
        void deleteUser_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).deleteUser(any());
        }

        @Test
        @DisplayName("Unauthenticated should get 403")
        void deleteUser_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).deleteUser(any());
        }
    }
}
