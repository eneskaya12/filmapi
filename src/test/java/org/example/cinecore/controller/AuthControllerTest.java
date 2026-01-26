package org.example.cinecore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cinecore.config.TestSecurityConfig;
import org.example.cinecore.model.dto.request.UserCreateRequest;
import org.example.cinecore.model.dto.request.UserLoginRequest;
import org.example.cinecore.model.dto.response.AuthenticationResponse;
import org.example.cinecore.security.JwtAuthenticationFilter;
import org.example.cinecore.service.AuthenticationService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    private UserCreateRequest registerRequest;
    private UserLoginRequest loginRequest;
    private AuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new UserCreateRequest("John Doe", "john@example.com", "password123");
        loginRequest = new UserLoginRequest("john@example.com", "password123");
        authResponse = new AuthenticationResponse("jwt-token-here");
    }

    @Nested
    @DisplayName("POST /api/auth/register - Public endpoint")
    class RegisterTests {

        @Test
        @DisplayName("Should register user and return 201 with token")
        void register_WithValidRequest_ShouldReturn201() throws Exception {
            when(authenticationService.register(any(UserCreateRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.payload.token").value("jwt-token-here"));

            verify(authenticationService, times(1)).register(any(UserCreateRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when fullname is blank")
        void register_WithBlankFullname_ShouldReturn400() throws Exception {
            UserCreateRequest invalidRequest = new UserCreateRequest("", "john@example.com", "password123");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(authenticationService, never()).register(any());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void register_WithInvalidEmail_ShouldReturn400() throws Exception {
            UserCreateRequest invalidRequest = new UserCreateRequest("John Doe", "invalid-email", "password123");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(authenticationService, never()).register(any());
        }

        @Test
        @DisplayName("Should return 400 when password is too short")
        void register_WithShortPassword_ShouldReturn400() throws Exception {
            UserCreateRequest invalidRequest = new UserCreateRequest("John Doe", "john@example.com", "short");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(authenticationService, never()).register(any());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login - Public endpoint")
    class LoginTests {

        @Test
        @DisplayName("Should login user and return token")
        void login_WithValidCredentials_ShouldReturn200() throws Exception {
            when(authenticationService.login(any(UserLoginRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.payload.token").value("jwt-token-here"));

            verify(authenticationService, times(1)).login(any(UserLoginRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when email is blank")
        void login_WithBlankEmail_ShouldReturn400() throws Exception {
            UserLoginRequest invalidRequest = new UserLoginRequest("", "password123");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(authenticationService, never()).login(any());
        }

        @Test
        @DisplayName("Should return 400 when password is blank")
        void login_WithBlankPassword_ShouldReturn400() throws Exception {
            UserLoginRequest invalidRequest = new UserLoginRequest("john@example.com", "");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(authenticationService, never()).login(any());
        }
    }
}
