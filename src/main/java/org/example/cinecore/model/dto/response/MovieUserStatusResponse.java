package org.example.cinecore.model.dto.response;

import lombok.Builder;

@Builder
public record MovieUserStatusResponse(
        Long movieId,
        String movieTitle,
        boolean isFavorite,
        boolean isWatched
) {
}
