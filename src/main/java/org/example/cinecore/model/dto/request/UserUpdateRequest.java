package org.example.cinecore.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.example.cinecore.model.enums.Role;

public record UserUpdateRequest(
        @Size(min = 3, max = 80, message = "Fullname must be between 3 and 80 characters")
        String fullname,

        @Email(message = "Email is not valid")
        String email,

        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        Role role
) {
}
