package org.example.filmapi.mapper;

import org.example.filmapi.model.dto.request.MovieCreateRequest;
import org.example.filmapi.model.dto.response.MovieResponse;
import org.example.filmapi.model.entity.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(MovieCreateRequest request);

    MovieResponse toMovieResponse(Movie movie);
}
