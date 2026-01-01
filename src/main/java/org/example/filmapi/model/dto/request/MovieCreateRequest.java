package org.example.filmapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.filmapi.model.enums.Language;

import java.time.Instant;

public record MovieCreateRequest(
        @NotBlank(message = "Movie title is required")
        String title,

        @NotBlank(message = "Movie description is required")
        String description,

        @NotNull(message = "Movie duration is required")
        Integer duration,

        @NotNull(message = "Movie language is required")
        Language language,

        @NotNull(message = "Movie imdb is required")
        Double imdb,

        @NotNull(message = "Movie release date is required")
        Instant releaseDate
) {
}
