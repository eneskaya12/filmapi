package org.example.cinecore.mapper;

import org.example.cinecore.model.dto.request.MovieCreateRequest;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.entity.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(MovieCreateRequest request);

    MovieResponse toMovieResponse(Movie movie);
}
