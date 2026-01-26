package org.example.cinecore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cinecore.config.TestSecurityConfig;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.enums.Language;
import org.example.cinecore.security.JwtAuthenticationFilter;
import org.example.cinecore.service.MovieCategoryService;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MovieCategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@Import(TestSecurityConfig.class)
class MovieCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieCategoryService movieCategoryService;

    private CategoryResponse categoryResponse;
    private MovieResponse movieResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Action")
                .build();

        movieResponse = MovieResponse.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .duration(148)
                .language(Language.EN)
                .imdb(8.8)
                .releaseDate(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/movies/{movieId}/categories/{categoryId} - ADMIN only")
    class AddCategoryToMovieTests {

        @Test
        @DisplayName("ADMIN should add category to movie")
        @WithMockUser(roles = "ADMIN")
        void addCategoryToMovie_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(movieCategoryService).addCategoryToMovie(1L, 2L);

            mockMvc.perform(post("/api/movies/1/categories/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Category added to movie successfully"));

            verify(movieCategoryService, times(1)).addCategoryToMovie(1L, 2L);
        }

        @Test
        @DisplayName("USER should get 403")
        @WithMockUser(roles = "USER")
        void addCategoryToMovie_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(post("/api/movies/1/categories/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(movieCategoryService, never()).addCategoryToMovie(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Unauthenticated should get 403")
        void addCategoryToMovie_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(post("/api/movies/1/categories/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(movieCategoryService, never()).addCategoryToMovie(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("DELETE /api/movies/{movieId}/categories/{categoryId} - ADMIN only")
    class RemoveCategoryFromMovieTests {

        @Test
        @DisplayName("ADMIN should remove category from movie")
        @WithMockUser(roles = "ADMIN")
        void removeCategoryFromMovie_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(movieCategoryService).removeCategoryFromMovie(1L, 2L);

            mockMvc.perform(delete("/api/movies/1/categories/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Category removed from movie successfully"));

            verify(movieCategoryService, times(1)).removeCategoryFromMovie(1L, 2L);
        }

        @Test
        @DisplayName("USER should get 403")
        @WithMockUser(roles = "USER")
        void removeCategoryFromMovie_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/movies/1/categories/2"))
                    .andExpect(status().isForbidden());

            verify(movieCategoryService, never()).removeCategoryFromMovie(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/movies/{movieId}/categories - Public")
    class GetCategoriesOfMovieTests {

        @Test
        @DisplayName("Anyone should get categories of movie")
        void getCategoriesOfMovie_WithoutAuth_ShouldReturn200() throws Exception {
            when(movieCategoryService.getCategoriesOfMovie(1L)).thenReturn(List.of(categoryResponse));

            mockMvc.perform(get("/api/movies/1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Categories retrieved successfully"))
                    .andExpect(jsonPath("$.payload[0].name").value("Action"));

            verify(movieCategoryService, times(1)).getCategoriesOfMovie(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/categories/{categoryId}/movies - Public")
    class GetMoviesOfCategoryTests {

        @Test
        @DisplayName("Anyone should get movies of category")
        void getMoviesOfCategory_WithoutAuth_ShouldReturn200() throws Exception {
            when(movieCategoryService.getMoviesOfCategory(1L)).thenReturn(List.of(movieResponse));

            mockMvc.perform(get("/api/categories/1/movies")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Movies retrieved successfully"))
                    .andExpect(jsonPath("$.payload[0].title").value("Inception"));

            verify(movieCategoryService, times(1)).getMoviesOfCategory(1L);
        }
    }
}
