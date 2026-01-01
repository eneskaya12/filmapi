package org.example.filmapi.model.dto.response;

import lombok.Builder;
import org.example.filmapi.model.enums.Role;

@Builder
public record UserResponse(
        Long id,
        String fullname,
        String email,
        Role role
) {
}
