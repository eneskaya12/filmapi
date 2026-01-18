package org.example.filmapi.service.impl;

import org.example.filmapi.exception.EntityNotFoundException;
import org.example.filmapi.mapper.MovieMapper;
import org.example.filmapi.model.dto.request.MovieCreateRequest;
import org.example.filmapi.model.dto.request.MovieUpdateRequest;
import org.example.filmapi.model.dto.response.MovieResponse;
import org.example.filmapi.model.dto.response.PagedResponse;
import org.example.filmapi.model.entity.Movie;
import org.example.filmapi.model.enums.Language;
import org.example.filmapi.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;
    private MovieResponse movieResponse;
    private MovieCreateRequest movieCreateRequest;
    private MovieUpdateRequest movieUpdateRequest;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .description("A thief who steals corporate secrets through dream-sharing technology.")
                .duration(148)
                .language(Language.EN)
                .imdb(8.8)
                .releaseDate(toInstant(2010, 7, 16))
                .build();

        movieResponse = MovieResponse.builder()
                .id(1L)
                .title("Inception")
                .description("A thief who steals corporate secrets through dream-sharing technology.")
                .duration(148)
                .language(Language.EN)
                .imdb(8.8)
                .releaseDate(toInstant(2010, 7, 16))
                .build();

        movieCreateRequest = new MovieCreateRequest("Inception", "A thief who steals corporate secrets through dream-sharing technology.", 148, Language.EN, 8.8, toInstant(2010, 7, 16));

        movieUpdateRequest = new MovieUpdateRequest("The Dark Knight", "Batman faces the Joker, a criminal mastermind who wants to plunge Gotham into anarchy.", 152, Language.EN, 9.0, toInstant(2008, 7, 18));
    }

    private Instant toInstant(int year, int month, int day) {
        return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    @Nested
    @DisplayName("addMovie method tests")
    class AddMovieTests {

        @Test
        @DisplayName("Add movie successfuly - all steps need to be done")
        void addMovie_ShouldSaveMovie() {
            when(movieMapper.toMovie(movieCreateRequest)).thenReturn(movie);
            when(movieRepository.save(movie)).thenReturn(movie);

            movieService.addMovie(movieCreateRequest);

            verify(movieMapper, times(1)).toMovie(movieCreateRequest);
            verify(movieRepository, times(1)).save(movie);
        }
    }

    @Nested
    @DisplayName("getMovieById method tests")
    class GetMovieByIdTests {

        @Test
        @DisplayName("find movie with existing id")
        void getMovieById_WithExistingId_ShouldReturnMovie() {
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(movieMapper.toMovieResponse(movie)).thenReturn(movieResponse);

            MovieResponse result = movieService.getMovieById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Inception");
            assertThat(result.description()).isEqualTo("A thief who steals corporate secrets through dream-sharing technology.");
            assertThat(result.duration()).isEqualTo(148);
            assertThat(result.language()).isEqualTo(Language.EN);
            assertThat(result.imdb()).isEqualTo(8.8);
            assertThat(result.releaseDate()).isEqualTo(toInstant(2010, 7, 16));

            verify(movieRepository, times(1)).findById(1L);
            verify(movieMapper, times(1)).toMovieResponse(movie);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException with non-existing Id")
        void getMovieById_WithNonExistingId_ShouldThrowException() {
            when(movieRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieService.getMovieById(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieRepository, times(1)).findById(999L);
            verify(movieMapper, never()).toMovieResponse(any());
        }
    }

    @Nested
    @DisplayName("getAllMovies method tests")
    class GetAllMoviesTest {

        @Test
        @DisplayName("should return paged all movies")
        void getAllMovies_ShouldReturnPagedResponse() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Movie> moviePage = new PageImpl<>(
                    List.of(movie),
                    pageable,
                    1
            );

            when(movieRepository.findAll(pageable)).thenReturn(moviePage);
            when(movieMapper.toMovieResponse(movie)).thenReturn(movieResponse);

            PagedResponse<MovieResponse> result = movieService.getAllMovies(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().title()).isEqualTo("Inception");
            assertThat(result.content().getFirst().description()).isEqualTo("A thief who steals corporate secrets through dream-sharing technology.");
            assertThat(result.content().getFirst().duration()).isEqualTo(148);
            assertThat(result.content().getFirst().language()).isEqualTo(Language.EN);
            assertThat(result.content().getFirst().imdb()).isEqualTo(8.8);
            assertThat(result.content().getFirst().releaseDate()).isEqualTo(toInstant(2010, 7, 16));

            verify(movieRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("updateMovie method tests")
    class UpdateMovieTests {

        @Test
        @DisplayName("update with different values - needs to be saved")
        void updateMovie_WithDifferentValues_ShouldSave() {
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(movieRepository.save(any(Movie.class))).thenReturn(movie);

            movieService.updateMovie(1L, movieUpdateRequest);

            verify(movieRepository, times(1)).save(movie);
        }

        @Test
        @DisplayName("update with non-existing id - should throw exception")
        void updateMovie_WithNonExistingId_ShouldThrowException() {
            when(movieRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieService.updateMovie(999L, movieUpdateRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteMovie method tests")
    class DeleteMovieTests {

        @Test
        @DisplayName("should delete existing movie")
        void deleteMovie_WithExistingId_ShouldDelete() {
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            doNothing().when(movieRepository).delete(movie);

            movieService.deleteMovie(1L);

            verify(movieRepository, times(1)).findById(1L);
            verify(movieRepository, times(1)).delete(movie);
        }

        @Test
        @DisplayName("delete movie with non-existing id - should throw exception")
        void deleteMovie_WithNonExistingId_ShouldThrowException() {
            when(movieRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieService.deleteMovie(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieRepository, never()).delete(any());
        }
    }
}
