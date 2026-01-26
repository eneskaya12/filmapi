package org.example.cinecore.model.dto.request;

public record MovieUserStatusRequest(
        Boolean isFavorite,
        Boolean isWatched
) {
}
