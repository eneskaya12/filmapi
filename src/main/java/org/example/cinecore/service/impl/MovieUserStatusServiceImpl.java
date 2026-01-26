package org.example.cinecore.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.MovieUserStatusMapper;
import org.example.cinecore.model.dto.request.MovieUserStatusRequest;
import org.example.cinecore.model.dto.response.MovieUserStatusResponse;
import org.example.cinecore.model.entity.Movie;
import org.example.cinecore.model.entity.MovieUserStatus;
import org.example.cinecore.model.entity.User;
import org.example.cinecore.repository.MovieRepository;
import org.example.cinecore.repository.MovieUserStatusRepository;
import org.example.cinecore.service.AuthenticationService;
import org.example.cinecore.service.MovieUserStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieUserStatusServiceImpl implements MovieUserStatusService {
    private final MovieUserStatusRepository movieUserStatusRepository;
    private final MovieRepository movieRepository;
    private final AuthenticationService authenticationService;
    private final MovieUserStatusMapper movieUserStatusMapper;

    @Override
    @Transactional
    public void updateStatus(Long movieId, MovieUserStatusRequest request) {
        User user = authenticationService.getAuthenticatedUser();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        MovieUserStatus status = movieUserStatusRepository
                .findByUserIdAndMovieId(user.getId(), movieId)
                .orElseGet(() -> MovieUserStatus.builder()
                        .user(user)
                        .movie(movie)
                        .isFavorite(false)
                        .isWatched(false)
                        .build());

        Optional.ofNullable(request.isFavorite()).ifPresent(status::setFavorite);
        Optional.ofNullable(request.isWatched()).ifPresent(status::setWatched);

        movieUserStatusRepository.save(status);
    }

    @Override
    public MovieUserStatusResponse getStatus(Long movieId) {
        User user = authenticationService.getAuthenticatedUser();

        if (!movieRepository.existsById(movieId)) {
            throw new EntityNotFoundException("Movie not found");
        }

        return movieUserStatusRepository.findByUserIdAndMovieId(user.getId(), movieId)
                .map(movieUserStatusMapper::toResponse)
                .orElseGet(() -> MovieUserStatusResponse.builder()
                        .movieId(movieId)
                        .movieTitle(movieRepository.findById(movieId).get().getTitle())
                        .isFavorite(false)
                        .isWatched(false)
                        .build());
    }

    @Override
    public List<MovieUserStatusResponse> getMyMovies() {
        User user = authenticationService.getAuthenticatedUser();
        return movieUserStatusRepository.findByUserId(user.getId())
                .stream()
                .map(movieUserStatusMapper::toResponse)
                .toList();
    }

    @Override
    public List<MovieUserStatusResponse> getMyFavorites() {
        User user = authenticationService.getAuthenticatedUser();
        return movieUserStatusRepository.findByUserIdAndIsFavoriteTrue(user.getId())
                .stream()
                .map(movieUserStatusMapper::toResponse)
                .toList();
    }

    @Override
    public List<MovieUserStatusResponse> getMyWatched() {
        User user = authenticationService.getAuthenticatedUser();
        return movieUserStatusRepository.findByUserIdAndIsWatchedTrue(user.getId())
                .stream()
                .map(movieUserStatusMapper::toResponse)
                .toList();
    }
}
