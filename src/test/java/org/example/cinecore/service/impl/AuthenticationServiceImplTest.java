package org.example.cinecore.service.impl;

import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.UserMapper;
import org.example.cinecore.model.dto.request.UserCreateRequest;
import org.example.cinecore.model.dto.request.UserLoginRequest;
import org.example.cinecore.model.dto.response.AuthenticationResponse;
import org.example.cinecore.model.entity.User;
import org.example.cinecore.model.enums.Role;
import org.example.cinecore.repository.UserRepository;
import org.example.cinecore.security.CustomUserDetails;
import org.example.cinecore.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private UserCreateRequest createRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullname("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        createRequest = new UserCreateRequest("John Doe", "john@example.com", "password123");
        loginRequest = new UserLoginRequest("john@example.com", "password123");
    }

    @Nested
    @DisplayName("register method tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register user and return token")
        void register_WithValidRequest_ShouldReturnToken() {
            when(userMapper.toUser(createRequest)).thenReturn(user);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(user)).thenReturn(user);
            when(jwtUtil.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");

            AuthenticationResponse result = authenticationService.register(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo("jwt-token");

            verify(userMapper, times(1)).toUser(createRequest);
            verify(passwordEncoder, times(1)).encode("password123");
            verify(userRepository, times(1)).save(user);
            verify(jwtUtil, times(1)).generateToken(any(CustomUserDetails.class));
        }
    }

    @Nested
    @DisplayName("login method tests")
    class LoginTests {

        @Test
        @DisplayName("Should login user and return token")
        void login_WithValidCredentials_ShouldReturnToken() {
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(jwtUtil.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");

            AuthenticationResponse result = authenticationService.login(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo("jwt-token");

            verify(userRepository, times(1)).findByEmail("john@example.com");
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
            verify(jwtUtil, times(1)).generateToken(any(CustomUserDetails.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void login_WithNonExistingEmail_ShouldThrowException() {
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            UserLoginRequest invalidRequest = new UserLoginRequest("unknown@example.com", "password123");

            assertThatThrownBy(() -> authenticationService.login(invalidRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found");

            verify(userRepository, times(1)).findByEmail("unknown@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateToken(any());
        }

        @Test
        @DisplayName("Should throw exception when password is invalid")
        void login_WithInvalidPassword_ShouldThrowException() {
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            UserLoginRequest invalidRequest = new UserLoginRequest("john@example.com", "wrongPassword");

            assertThatThrownBy(() -> authenticationService.login(invalidRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Invalid password");

            verify(userRepository, times(1)).findByEmail("john@example.com");
            verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
            verify(jwtUtil, never()).generateToken(any());
        }
    }
}
