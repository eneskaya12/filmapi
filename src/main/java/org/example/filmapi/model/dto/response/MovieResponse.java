package org.example.filmapi.model.dto.response;

import lombok.Builder;
import org.example.filmapi.model.enums.Language;

import java.time.Instant;

@Builder
public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer duration,
        Language language,
        Double imdb,
        Instant releaseDate
) {
}
