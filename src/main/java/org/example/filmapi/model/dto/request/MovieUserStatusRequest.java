package org.example.filmapi.model.dto.request;

public record MovieUserStatusRequest(
        Boolean isFavorite,
        Boolean isWatched
) {
}
