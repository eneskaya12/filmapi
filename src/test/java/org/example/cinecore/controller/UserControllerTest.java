package org.example.cinecore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cinecore.config.TestSecurityConfig;
import org.example.cinecore.mapper.UserMapper;
import org.example.cinecore.model.dto.request.UserAdminUpdateRequest;
import org.example.cinecore.model.dto.request.UserUpdateRequest;
import org.example.cinecore.model.dto.response.UserResponse;
import org.example.cinecore.model.entity.User;
import org.example.cinecore.model.enums.Role;
import org.example.cinecore.security.JwtAuthenticationFilter;
import org.example.cinecore.service.AuthenticationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private User user;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullname("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .fullname("John Doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();

        updateRequest = new UserUpdateRequest("Jane Doe", "jane@example.com", "newPassword123");
    }

    @Nested
    @DisplayName("GET /api/users/profile - Authenticated endpoint")
    class GetMyProfileTests {

        @Test
        @DisplayName("Authenticated user should get own profile")
        @WithMockUser(username = "john@example.com", roles = "USER")
        void getMyProfile_WhenAuthenticated_ShouldReturn200() throws Exception {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(userMapper.toUserResponse(user)).thenReturn(userResponse);

            mockMvc.perform(get("/api/users/profile")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Profile retrieved successfully"))
                    .andExpect(jsonPath("$.payload.fullname").value("John Doe"))
                    .andExpect(jsonPath("$.payload.email").value("john@example.com"));

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(userMapper, times(1)).toUserResponse(user);
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void getMyProfile_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/profile")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(authenticationService, never()).getAuthenticatedUser();
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/profile - Authenticated endpoint")
    class UpdateMyProfileTests {

        @Test
        @DisplayName("Authenticated user should update own profile")
        @WithMockUser(username = "john@example.com", roles = "USER")
        void updateMyProfile_WhenAuthenticated_ShouldReturn200() throws Exception {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            doNothing().when(userService).updateUserByUser(eq(1L), any(UserUpdateRequest.class));

            mockMvc.perform(patch("/api/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Profile updated successfully"));

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(userService, times(1)).updateUserByUser(eq(1L), any(UserUpdateRequest.class));
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void updateMyProfile_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(patch("/api/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());

            verify(userService, never()).updateUserByUser(any(), any());
        }
    }
}
