package org.example.filmapi.service.impl;

import org.example.filmapi.exception.EntityNotFoundException;
import org.example.filmapi.mapper.UserMapper;
import org.example.filmapi.model.dto.request.UserUpdateRequest;
import org.example.filmapi.model.dto.response.UserResponse;
import org.example.filmapi.model.entity.User;
import org.example.filmapi.model.enums.Role;
import org.example.filmapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

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

        updateRequest = new UserUpdateRequest("Jane Doe", "jane@example.com", "newPassword", Role.ADMIN);
    }

    @Nested
    @DisplayName("getUserById method tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found")
        void getUserById_WithExistingId_ShouldReturnUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userMapper.toUserResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.fullname()).isEqualTo("John Doe");
            assertThat(result.email()).isEqualTo("john@example.com");
            assertThat(result.role()).isEqualTo(Role.USER);

            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).toUserResponse(user);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getUserById_WithNonExistingId_ShouldThrowException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found");

            verify(userRepository, times(1)).findById(999L);
            verify(userMapper, never()).toUserResponse(any());
        }
    }

    @Nested
    @DisplayName("getAllUsers method tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void getAllUsers_ShouldReturnUserList() {
            User user2 = User.builder()
                    .id(2L)
                    .fullname("Jane Doe")
                    .email("jane@example.com")
                    .role(Role.ADMIN)
                    .build();

            UserResponse userResponse2 = UserResponse.builder()
                    .id(2L)
                    .fullname("Jane Doe")
                    .email("jane@example.com")
                    .role(Role.ADMIN)
                    .build();

            when(userRepository.findAll()).thenReturn(List.of(user, user2));
            when(userMapper.toUserResponse(user)).thenReturn(userResponse);
            when(userMapper.toUserResponse(user2)).thenReturn(userResponse2);

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).fullname()).isEqualTo("John Doe");
            assertThat(result.get(1).fullname()).isEqualTo("Jane Doe");

            verify(userRepository, times(1)).findAll();
            verify(userMapper, times(2)).toUserResponse(any(User.class));
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).isEmpty();

            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("updateUser method tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user with new password")
        void updateUser_WithPassword_ShouldEncodeAndSave() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.updateUser(1L, updateRequest);

            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).updateUserFromRequest(updateRequest, user);
            verify(passwordEncoder, times(1)).encode("newPassword");
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should update user without password change")
        void updateUser_WithoutPassword_ShouldNotEncodePassword() {
            UserUpdateRequest requestWithoutPassword = new UserUpdateRequest("Jane Doe", "jane@example.com", null, Role.USER);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.updateUser(1L, requestWithoutPassword);

            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).updateUserFromRequest(requestWithoutPassword, user);
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void updateUser_WithNonExistingId_ShouldThrowException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found");

            verify(userRepository, times(1)).findById(999L);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteUser method tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete existing user")
        void deleteUser_WithExistingId_ShouldDelete() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            doNothing().when(userRepository).delete(user);

            userService.deleteUser(1L);

            verify(userRepository, times(1)).findById(1L);
            verify(userRepository, times(1)).delete(user);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void deleteUser_WithNonExistingId_ShouldThrowException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found");

            verify(userRepository, times(1)).findById(999L);
            verify(userRepository, never()).delete(any());
        }
    }
}
