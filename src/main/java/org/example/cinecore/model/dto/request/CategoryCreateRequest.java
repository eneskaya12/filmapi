package org.example.cinecore.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "Category name is required")
        String name
) {
}
