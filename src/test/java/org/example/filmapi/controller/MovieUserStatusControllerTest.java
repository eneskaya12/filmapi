package org.example.filmapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.filmapi.config.TestSecurityConfig;
import org.example.filmapi.model.dto.request.MovieUserStatusRequest;
import org.example.filmapi.model.dto.response.MovieUserStatusResponse;
import org.example.filmapi.security.JwtAuthenticationFilter;
import org.example.filmapi.service.MovieUserStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MovieUserStatusController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@Import(TestSecurityConfig.class)
class MovieUserStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieUserStatusService movieUserStatusService;

    private MovieUserStatusResponse statusResponse;
    private MovieUserStatusRequest statusRequest;

    @BeforeEach
    void setUp() {
        statusResponse = MovieUserStatusResponse.builder()
                .movieId(1L)
                .movieTitle("Inception")
                .isFavorite(true)
                .isWatched(true)
                .build();

        statusRequest = new MovieUserStatusRequest(true, true);
    }

    @Nested
    @DisplayName("PUT /api/users/profile/movies/{movieId}/status - Authenticated")
    class UpdateStatusTests {

        @Test
        @DisplayName("Authenticated user should update movie status")
        @WithMockUser(roles = "USER")
        void updateStatus_WhenAuthenticated_ShouldReturn200() throws Exception {
            doNothing().when(movieUserStatusService).updateStatus(eq(1L), any(MovieUserStatusRequest.class));

            mockMvc.perform(put("/api/users/profile/movies/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Movie status updated successfully"));

            verify(movieUserStatusService, times(1)).updateStatus(eq(1L), any(MovieUserStatusRequest.class));
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void updateStatus_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(put("/api/users/profile/movies/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isForbidden());

            verify(movieUserStatusService, never()).updateStatus(any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/users/profile/movies/{movieId}/status - Authenticated")
    class GetStatusTests {

        @Test
        @DisplayName("Authenticated user should get movie status")
        @WithMockUser(roles = "USER")
        void getStatus_WhenAuthenticated_ShouldReturn200() throws Exception {
            when(movieUserStatusService.getStatus(1L)).thenReturn(statusResponse);

            mockMvc.perform(get("/api/users/profile/movies/1/status")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Movie status retrieved successfully"))
                    .andExpect(jsonPath("$.payload.movieTitle").value("Inception"))
                    .andExpect(jsonPath("$.payload.isFavorite").value(true))
                    .andExpect(jsonPath("$.payload.isWatched").value(true));

            verify(movieUserStatusService, times(1)).getStatus(1L);
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void getStatus_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/profile/movies/1/status")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(movieUserStatusService, never()).getStatus(any());
        }
    }

    @Nested
    @DisplayName("GET /api/users/profile/movies - Authenticated")
    class GetMyMoviesTests {

        @Test
        @DisplayName("Authenticated user should get all movie statuses")
        @WithMockUser(roles = "USER")
        void getMyMovies_WhenAuthenticated_ShouldReturn200() throws Exception {
            when(movieUserStatusService.getMyMovies()).thenReturn(List.of(statusResponse));

            mockMvc.perform(get("/api/users/profile/movies")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Movies retrieved successfully"))
                    .andExpect(jsonPath("$.payload[0].movieTitle").value("Inception"));

            verify(movieUserStatusService, times(1)).getMyMovies();
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void getMyMovies_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/profile/movies")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(movieUserStatusService, never()).getMyMovies();
        }
    }

    @Nested
    @DisplayName("GET /api/users/profile/movies/favorites - Authenticated")
    class GetMyFavoritesTests {

        @Test
        @DisplayName("Authenticated user should get favorite movies")
        @WithMockUser(roles = "USER")
        void getMyFavorites_WhenAuthenticated_ShouldReturn200() throws Exception {
            when(movieUserStatusService.getMyFavorites()).thenReturn(List.of(statusResponse));

            mockMvc.perform(get("/api/users/profile/movies/favorites")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Favorite movies retrieved successfully"))
                    .andExpect(jsonPath("$.payload[0].isFavorite").value(true));

            verify(movieUserStatusService, times(1)).getMyFavorites();
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void getMyFavorites_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/profile/movies/favorites")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(movieUserStatusService, never()).getMyFavorites();
        }
    }

    @Nested
    @DisplayName("GET /api/users/profile/movies/watched - Authenticated")
    class GetMyWatchedTests {

        @Test
        @DisplayName("Authenticated user should get watched movies")
        @WithMockUser(roles = "USER")
        void getMyWatched_WhenAuthenticated_ShouldReturn200() throws Exception {
            when(movieUserStatusService.getMyWatched()).thenReturn(List.of(statusResponse));

            mockMvc.perform(get("/api/users/profile/movies/watched")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Watched movies retrieved successfully"))
                    .andExpect(jsonPath("$.payload[0].isWatched").value(true));

            verify(movieUserStatusService, times(1)).getMyWatched();
        }

        @Test
        @DisplayName("Unauthenticated user should get 403")
        void getMyWatched_WhenNotAuthenticated_ShouldReturn403() throws Exception {
            mockMvc.perform(get("/api/users/profile/movies/watched")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(movieUserStatusService, never()).getMyWatched();
        }
    }
}
