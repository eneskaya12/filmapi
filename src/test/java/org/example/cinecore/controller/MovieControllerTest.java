package org.example.cinecore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cinecore.config.TestSecurityConfig;
import org.example.cinecore.model.dto.request.MovieCreateRequest;
import org.example.cinecore.model.dto.request.MovieUpdateRequest;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.example.cinecore.model.enums.Language;
import org.example.cinecore.security.JwtAuthenticationFilter;
import org.example.cinecore.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MovieController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
    )
@Import(TestSecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    private MovieResponse movieResponse;
    private MovieCreateRequest movieCreateRequest;
    private MovieUpdateRequest movieUpdateRequest;

    @BeforeEach
    void setUp() {
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
    @DisplayName("GET /api/movies - public endpoint tests")
    class PublicEndpointTests {

        @Test
        @DisplayName("anyone can list movies")
        void getAllMovies_WithoutAuth_ShouldReturn200() throws Exception {
            PagedResponse<MovieResponse> pagedResponse = PagedResponse.<MovieResponse>builder()
                    .content(List.of(movieResponse))
                    .pageNumber(0)
                    .pageSize(5)
                    .totalElements(1)
                    .totalPages(1)
                    .last(true)
                    .build();

            when(movieService.getAllMovies(any())).thenReturn(pagedResponse);

            mockMvc.perform(get("/api/movies").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Movies retrieved successfully"))
                    .andExpect(jsonPath("$.payload.content[0].title").value("Inception"));

            verify(movieService, times(1)).getAllMovies(any());
        }

        @Test
        @DisplayName("get movie by id - anyone can list")
        void getMovieById_WithoutAuth_ShouldReturn200() throws Exception {
            when(movieService.getMovieById(1L)).thenReturn(movieResponse);

            mockMvc.perform(get("/api/movies/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.id").value(1))
                    .andExpect(jsonPath("$.payload.title").value("Inception"));

            verify(movieService, times(1)).getMovieById(1L);
        }
    }

    @Nested
    @DisplayName("POST/PATCH/DELETE - ADMIN required endpoint tests")
    class AdminEndpointTests {

        @Test
        @DisplayName("ADMIN user should add movie")
        @WithMockUser(roles = "ADMIN")
        void addMovie_WithAdminRole_ShouldReturn201() throws Exception {
            doNothing().when(movieService).addMovie(any(MovieCreateRequest.class));

            mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movieCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Movie added successfully"));

            verify(movieService, times(1)).addMovie(any(MovieCreateRequest.class));
        }

        @Test
        @DisplayName("Normal user should not add movie - 403 forbidden")
        @WithMockUser(roles = "USER")
        void addMovie_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movieCreateRequest)))
                    .andExpect(status().isForbidden());

            verify(movieService, never()).addMovie(any());
        }

        @Test
        @DisplayName("Unsigned user should not add movie - 403 forbidden")
        void addMovie_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(movieCreateRequest)))
                    .andExpect(status().isForbidden());

            verify(movieService, never()).addMovie(any());
        }

        @Test
        @DisplayName("ADMIN user should update movie")
        @WithMockUser(roles = "ADMIN")
        void updateMovie_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(movieService).updateMovie(eq(1L), any(MovieUpdateRequest.class));

            mockMvc.perform(patch("/api/movies/1").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(movieUpdateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Movie updated successfully"));

            verify(movieService, times(1)).updateMovie(eq(1L), any(MovieUpdateRequest.class));
        }

        @Test
        @DisplayName("Normal user should not update movie - 403 forbidden")
        @WithMockUser(roles = "USER")
        void updateMovie_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(patch("/api/movies/1").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(movieUpdateRequest)))
                    .andExpect(status().isForbidden());

            verify(movieService, never()).updateMovie(anyLong(), any(MovieUpdateRequest.class));
        }

        @Test
        @DisplayName("Unsigned user should not update movie - 403 forbidden")
        void updateMovie_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(patch("/api/movies/1").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(movieUpdateRequest)))
                    .andExpect(status().isForbidden());

            verify(movieService, never()).updateMovie(anyLong(), any(MovieUpdateRequest.class));
        }

        @Test
        @DisplayName("ADMIN user should delete movie")
        @WithMockUser(roles = "ADMIN")
        void deleteMovie_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(movieService).deleteMovie(1L);

            mockMvc.perform(delete("/api/movies/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Movie deleted successfully"));

            verify(movieService, times(1)).deleteMovie(1L);
        }

        @Test
        @DisplayName("Normal user should not delete movie - 403 forbidden")
        @WithMockUser(roles = "USER")
        void deleteMovie_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/movies/1"))
                    .andExpect(status().isForbidden());

            verify(movieService, never()).deleteMovie(any());
        }

        @Test
        @DisplayName("Unsigned user should not delete movie - 403 forbidden")
        void deleteMovie_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/movies/1"))
                    .andExpect(status().isForbidden());

            verify(movieService, never()).deleteMovie(any());
        }
    }
}
