package org.example.filmapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.filmapi.exception.EntityNotFoundException;
import org.example.filmapi.mapper.MovieMapper;
import org.example.filmapi.model.dto.request.MovieCreateRequest;
import org.example.filmapi.model.dto.request.MovieUpdateRequest;
import org.example.filmapi.model.dto.response.MovieResponse;
import org.example.filmapi.model.dto.response.PagedResponse;
import org.example.filmapi.model.entity.Movie;
import org.example.filmapi.repository.MovieRepository;
import org.example.filmapi.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public void addMovie(MovieCreateRequest request) {
        Movie movie = movieMapper.toMovie(request);
        movieRepository.save(movie);
    }

    @Override
    public MovieResponse getMovieById(Long id) {
        Movie movie = getMovie(id);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public PagedResponse<MovieResponse> getAllMovies(Pageable pageable) {
        Page<MovieResponse> page = movieRepository.findAll(pageable)
                .map(movieMapper::toMovieResponse);
        return PagedResponse.of(page);
    }

    @Override
    public void updateMovie(Long id, MovieUpdateRequest request) {
        Movie movie = getMovie(id);

        Optional.ofNullable(request.title())
                .filter(title -> !title.isBlank())
                .filter(title -> !title.equals(movie.getTitle()))
                .ifPresent(movie::setTitle);

        Optional.ofNullable(request.description())
                .filter(description -> !description.equals(movie.getDescription()))
                .ifPresent(movie::setDescription);

        Optional.ofNullable(request.duration())
                .filter(duration -> !duration.equals(movie.getDuration()))
                .ifPresent(movie::setDuration);

        Optional.ofNullable(request.language())
                .filter(language -> !language.equals(movie.getLanguage()))
                .ifPresent(movie::setLanguage);

        Optional.ofNullable(request.imdb())
                .filter(imdb -> !imdb.equals(movie.getImdb()))
                .ifPresent(movie::setImdb);

        Optional.ofNullable(request.releaseDate())
                .filter(releaseDate -> !releaseDate.equals(movie.getReleaseDate()))
                .ifPresent(movie::setReleaseDate);

        movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = getMovie(id);
        movieRepository.delete(movie);
    }

    private Movie getMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
    }

}
