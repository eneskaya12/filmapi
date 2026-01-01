package org.example.filmapi.mapper;

import org.example.filmapi.model.dto.request.MovieCreateRequest;
import org.example.filmapi.model.dto.response.MovieResponse;
import org.example.filmapi.model.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public Movie toMovie(MovieCreateRequest request) {
        return Movie.builder()
                .title(request.title())
                .description(request.description())
                .duration(request.duration())
                .language(request.language())
                .imdb(request.imdb())
                .releaseDate(request.releaseDate())
                .build();
    }

    public MovieResponse toMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .language(movie.getLanguage())
                .imdb(movie.getImdb())
                .releaseDate(movie.getReleaseDate())
                .build();
    }
}
