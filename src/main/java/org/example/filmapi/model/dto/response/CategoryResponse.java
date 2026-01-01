package org.example.filmapi.model.dto.response;

import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name
) {
}
