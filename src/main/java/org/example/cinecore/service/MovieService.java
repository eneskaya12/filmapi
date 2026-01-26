package org.example.cinecore.service;

import org.example.cinecore.model.dto.request.MovieCreateRequest;
import org.example.cinecore.model.dto.request.MovieUpdateRequest;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface MovieService {
    void addMovie(MovieCreateRequest request);
    MovieResponse getMovieById(Long id);
    PagedResponse<MovieResponse> getAllMovies(Pageable pageable);
    void updateMovie(Long id, MovieUpdateRequest request);
    void deleteMovie(Long id);
}
