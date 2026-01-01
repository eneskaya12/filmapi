package org.example.filmapi.service;

import org.example.filmapi.model.dto.request.MovieUserStatusRequest;
import org.example.filmapi.model.dto.response.MovieUserStatusResponse;

import java.util.List;

public interface MovieUserStatusService {
    void updateStatus(Long movieId, MovieUserStatusRequest request);
    MovieUserStatusResponse getStatus(Long movieId);
    List<MovieUserStatusResponse> getMyMovies();
    List<MovieUserStatusResponse> getMyFavorites();
    List<MovieUserStatusResponse> getMyWatched();
}
