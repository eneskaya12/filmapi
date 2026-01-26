package org.example.cinecore.mapper;

import org.example.cinecore.model.dto.response.MovieUserStatusResponse;
import org.example.cinecore.model.entity.MovieUserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieUserStatusMapper {

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "favorite", target = "isFavorite")
    @Mapping(source = "watched", target = "isWatched")
    MovieUserStatusResponse toResponse(MovieUserStatus status);
}
