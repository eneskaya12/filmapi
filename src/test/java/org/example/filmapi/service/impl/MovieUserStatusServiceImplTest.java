package org.example.filmapi.service.impl;

import org.example.filmapi.exception.EntityNotFoundException;
import org.example.filmapi.mapper.MovieUserStatusMapper;
import org.example.filmapi.model.dto.request.MovieUserStatusRequest;
import org.example.filmapi.model.dto.response.MovieUserStatusResponse;
import org.example.filmapi.model.entity.Movie;
import org.example.filmapi.model.entity.MovieUserStatus;
import org.example.filmapi.model.entity.User;
import org.example.filmapi.model.enums.Language;
import org.example.filmapi.model.enums.Role;
import org.example.filmapi.repository.MovieRepository;
import org.example.filmapi.repository.MovieUserStatusRepository;
import org.example.filmapi.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieUserStatusServiceImplTest {

    @Mock
    private MovieUserStatusRepository movieUserStatusRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MovieUserStatusMapper movieUserStatusMapper;

    @InjectMocks
    private MovieUserStatusServiceImpl movieUserStatusService;

    private User user;
    private Movie movie;
    private MovieUserStatus movieUserStatus;
    private MovieUserStatusResponse statusResponse;
    private MovieUserStatusRequest statusRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullname("John Doe")
                .email("john@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .duration(148)
                .language(Language.EN)
                .imdb(8.8)
                .releaseDate(Instant.now())
                .build();

        movieUserStatus = MovieUserStatus.builder()
                .id(1L)
                .user(user)
                .movie(movie)
                .isFavorite(true)
                .isWatched(true)
                .build();

        statusResponse = MovieUserStatusResponse.builder()
                .movieId(1L)
                .movieTitle("Inception")
                .isFavorite(true)
                .isWatched(true)
                .build();

        statusRequest = new MovieUserStatusRequest(true, true);
    }

    @Nested
    @DisplayName("updateStatus method tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update existing status")
        void updateStatus_WithExistingStatus_ShouldUpdate() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(movieUserStatusRepository.findByUserIdAndMovieId(1L, 1L))
                    .thenReturn(Optional.of(movieUserStatus));
            when(movieUserStatusRepository.save(any(MovieUserStatus.class))).thenReturn(movieUserStatus);

            movieUserStatusService.updateStatus(1L, statusRequest);

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(movieRepository, times(1)).findById(1L);
            verify(movieUserStatusRepository, times(1)).findByUserIdAndMovieId(1L, 1L);
            verify(movieUserStatusRepository, times(1)).save(any(MovieUserStatus.class));
        }

        @Test
        @DisplayName("Should create new status when not exists")
        void updateStatus_WithNewStatus_ShouldCreate() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(movieUserStatusRepository.findByUserIdAndMovieId(1L, 1L))
                    .thenReturn(Optional.empty());
            when(movieUserStatusRepository.save(any(MovieUserStatus.class))).thenReturn(movieUserStatus);

            movieUserStatusService.updateStatus(1L, statusRequest);

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(movieRepository, times(1)).findById(1L);
            verify(movieUserStatusRepository, times(1)).findByUserIdAndMovieId(1L, 1L);
            verify(movieUserStatusRepository, times(1)).save(any(MovieUserStatus.class));
        }

        @Test
        @DisplayName("Should throw exception when movie not found")
        void updateStatus_WithNonExistingMovie_ShouldThrowException() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieUserStatusService.updateStatus(999L, statusRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieUserStatusRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getStatus method tests")
    class GetStatusTests {

        @Test
        @DisplayName("Should return existing status")
        void getStatus_WithExistingStatus_ShouldReturnStatus() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieRepository.existsById(1L)).thenReturn(true);
            when(movieUserStatusRepository.findByUserIdAndMovieId(1L, 1L))
                    .thenReturn(Optional.of(movieUserStatus));
            when(movieUserStatusMapper.toResponse(movieUserStatus)).thenReturn(statusResponse);

            MovieUserStatusResponse result = movieUserStatusService.getStatus(1L);

            assertThat(result).isNotNull();
            assertThat(result.movieId()).isEqualTo(1L);
            assertThat(result.movieTitle()).isEqualTo("Inception");
            assertThat(result.isFavorite()).isTrue();
            assertThat(result.isWatched()).isTrue();

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(movieRepository, times(1)).existsById(1L);
            verify(movieUserStatusRepository, times(1)).findByUserIdAndMovieId(1L, 1L);
        }

        @Test
        @DisplayName("Should return default status when not exists")
        void getStatus_WithNoStatus_ShouldReturnDefault() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieRepository.existsById(1L)).thenReturn(true);
            when(movieUserStatusRepository.findByUserIdAndMovieId(1L, 1L))
                    .thenReturn(Optional.empty());
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

            MovieUserStatusResponse result = movieUserStatusService.getStatus(1L);

            assertThat(result).isNotNull();
            assertThat(result.movieId()).isEqualTo(1L);
            assertThat(result.movieTitle()).isEqualTo("Inception");
            assertThat(result.isFavorite()).isFalse();
            assertThat(result.isWatched()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when movie not found")
        void getStatus_WithNonExistingMovie_ShouldThrowException() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> movieUserStatusService.getStatus(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieUserStatusRepository, never()).findByUserIdAndMovieId(any(), any());
        }
    }

    @Nested
    @DisplayName("getMyMovies method tests")
    class GetMyMoviesTests {

        @Test
        @DisplayName("Should return all user movie statuses")
        void getMyMovies_ShouldReturnAllStatuses() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieUserStatusRepository.findByUserId(1L)).thenReturn(List.of(movieUserStatus));
            when(movieUserStatusMapper.toResponse(movieUserStatus)).thenReturn(statusResponse);

            List<MovieUserStatusResponse> result = movieUserStatusService.getMyMovies();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).movieTitle()).isEqualTo("Inception");

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(movieUserStatusRepository, times(1)).findByUserId(1L);
        }

        @Test
        @DisplayName("Should return empty list when no statuses")
        void getMyMovies_WhenNoStatuses_ShouldReturnEmptyList() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieUserStatusRepository.findByUserId(1L)).thenReturn(List.of());

            List<MovieUserStatusResponse> result = movieUserStatusService.getMyMovies();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getMyFavorites method tests")
    class GetMyFavoritesTests {

        @Test
        @DisplayName("Should return only favorite movies")
        void getMyFavorites_ShouldReturnFavorites() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieUserStatusRepository.findByUserIdAndIsFavoriteTrue(1L))
                    .thenReturn(List.of(movieUserStatus));
            when(movieUserStatusMapper.toResponse(movieUserStatus)).thenReturn(statusResponse);

            List<MovieUserStatusResponse> result = movieUserStatusService.getMyFavorites();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).isFavorite()).isTrue();

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(movieUserStatusRepository, times(1)).findByUserIdAndIsFavoriteTrue(1L);
        }
    }

    @Nested
    @DisplayName("getMyWatched method tests")
    class GetMyWatchedTests {

        @Test
        @DisplayName("Should return only watched movies")
        void getMyWatched_ShouldReturnWatched() {
            when(authenticationService.getAuthenticatedUser()).thenReturn(user);
            when(movieUserStatusRepository.findByUserIdAndIsWatchedTrue(1L))
                    .thenReturn(List.of(movieUserStatus));
            when(movieUserStatusMapper.toResponse(movieUserStatus)).thenReturn(statusResponse);

            List<MovieUserStatusResponse> result = movieUserStatusService.getMyWatched();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).isWatched()).isTrue();

            verify(authenticationService, times(1)).getAuthenticatedUser();
            verify(movieUserStatusRepository, times(1)).findByUserIdAndIsWatchedTrue(1L);
        }
    }
}
