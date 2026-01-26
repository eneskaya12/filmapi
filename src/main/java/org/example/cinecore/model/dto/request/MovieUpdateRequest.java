package org.example.cinecore.model.dto.request;

import org.example.cinecore.model.enums.Language;

import java.time.Instant;

public record MovieUpdateRequest(
        String title,
        String description,
        Integer duration,
        Language language,
        Double imdb,
        Instant releaseDate
) {
}
