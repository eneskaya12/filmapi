package org.example.cinecore.model.dto.response;

import lombok.Builder;
import org.example.cinecore.model.enums.Role;

@Builder
public record UserResponse(
        Long id,
        String fullname,
        String email,
        Role role
) {
}
