package org.example.cinecore.service.impl;

import org.example.cinecore.exception.DuplicateNameException;
import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.CategoryMapper;
import org.example.cinecore.mapper.MovieMapper;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.entity.Category;
import org.example.cinecore.model.entity.Movie;
import org.example.cinecore.model.entity.MovieCategory;
import org.example.cinecore.model.enums.Language;
import org.example.cinecore.repository.CategoryRepository;
import org.example.cinecore.repository.MovieCategoryRepository;
import org.example.cinecore.repository.MovieRepository;
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
class MovieCategoryServiceImplTest {

    @Mock
    private MovieCategoryRepository movieCategoryRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private MovieCategoryServiceImpl movieCategoryService;

    private Movie movie;
    private Category category;
    private MovieCategory movieCategory;
    private MovieResponse movieResponse;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .duration(148)
                .language(Language.EN)
                .imdb(8.8)
                .releaseDate(Instant.now())
                .build();

        category = Category.builder()
                .id(1L)
                .name("Action")
                .build();

        movieCategory = MovieCategory.builder()
                .id(1L)
                .movie(movie)
                .category(category)
                .build();

        movieResponse = MovieResponse.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .duration(148)
                .language(Language.EN)
                .imdb(8.8)
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Action")
                .build();
    }

    @Nested
    @DisplayName("addCategoryToMovie method tests")
    class AddCategoryToMovieTests {

        @Test
        @DisplayName("Should add category to movie successfully")
        void addCategoryToMovie_WithValidIds_ShouldSave() {
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(movieCategoryRepository.existsByMovieIdAndCategoryId(1L, 1L)).thenReturn(false);
            when(movieCategoryRepository.save(any(MovieCategory.class))).thenReturn(movieCategory);

            movieCategoryService.addCategoryToMovie(1L, 1L);

            verify(movieRepository, times(1)).findById(1L);
            verify(categoryRepository, times(1)).findById(1L);
            verify(movieCategoryRepository, times(1)).existsByMovieIdAndCategoryId(1L, 1L);
            verify(movieCategoryRepository, times(1)).save(any(MovieCategory.class));
        }

        @Test
        @DisplayName("Should throw exception when movie not found")
        void addCategoryToMovie_WithNonExistingMovie_ShouldThrowException() {
            when(movieRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieCategoryService.addCategoryToMovie(999L, 1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieRepository, times(1)).findById(999L);
            verify(categoryRepository, never()).findById(any());
            verify(movieCategoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void addCategoryToMovie_WithNonExistingCategory_ShouldThrowException() {
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieCategoryService.addCategoryToMovie(1L, 999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Category not found");

            verify(movieRepository, times(1)).findById(1L);
            verify(categoryRepository, times(1)).findById(999L);
            verify(movieCategoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when relation already exists")
        void addCategoryToMovie_WhenAlreadyExists_ShouldThrowException() {
            when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(movieCategoryRepository.existsByMovieIdAndCategoryId(1L, 1L)).thenReturn(true);

            assertThatThrownBy(() -> movieCategoryService.addCategoryToMovie(1L, 1L))
                    .isInstanceOf(DuplicateNameException.class)
                    .hasMessage("This category is already assigned to the movie");

            verify(movieCategoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeCategoryFromMovie method tests")
    class RemoveCategoryFromMovieTests {

        @Test
        @DisplayName("Should remove category from movie successfully")
        void removeCategoryFromMovie_WithExistingRelation_ShouldDelete() {
            when(movieCategoryRepository.findByMovieIdAndCategoryId(1L, 1L))
                    .thenReturn(Optional.of(movieCategory));
            doNothing().when(movieCategoryRepository).delete(movieCategory);

            movieCategoryService.removeCategoryFromMovie(1L, 1L);

            verify(movieCategoryRepository, times(1)).findByMovieIdAndCategoryId(1L, 1L);
            verify(movieCategoryRepository, times(1)).delete(movieCategory);
        }

        @Test
        @DisplayName("Should throw exception when relation not found")
        void removeCategoryFromMovie_WithNonExistingRelation_ShouldThrowException() {
            when(movieCategoryRepository.findByMovieIdAndCategoryId(1L, 1L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieCategoryService.removeCategoryFromMovie(1L, 1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie-Category relation not found");

            verify(movieCategoryRepository, times(1)).findByMovieIdAndCategoryId(1L, 1L);
            verify(movieCategoryRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("getCategoriesOfMovie method tests")
    class GetCategoriesOfMovieTests {

        @Test
        @DisplayName("Should return categories of movie")
        void getCategoriesOfMovie_WithExistingMovie_ShouldReturnCategories() {
            when(movieRepository.existsById(1L)).thenReturn(true);
            when(movieCategoryRepository.findByMovieId(1L)).thenReturn(List.of(movieCategory));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            List<CategoryResponse> result = movieCategoryService.getCategoriesOfMovie(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Action");

            verify(movieRepository, times(1)).existsById(1L);
            verify(movieCategoryRepository, times(1)).findByMovieId(1L);
            verify(categoryMapper, times(1)).toCategoryResponse(category);
        }

        @Test
        @DisplayName("Should throw exception when movie not found")
        void getCategoriesOfMovie_WithNonExistingMovie_ShouldThrowException() {
            when(movieRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> movieCategoryService.getCategoriesOfMovie(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found");

            verify(movieRepository, times(1)).existsById(999L);
            verify(movieCategoryRepository, never()).findByMovieId(any());
        }
    }

    @Nested
    @DisplayName("getMoviesOfCategory method tests")
    class GetMoviesOfCategoryTests {

        @Test
        @DisplayName("Should return movies of category")
        void getMoviesOfCategory_WithExistingCategory_ShouldReturnMovies() {
            when(categoryRepository.existsById(1L)).thenReturn(true);
            when(movieCategoryRepository.findByCategoryId(1L)).thenReturn(List.of(movieCategory));
            when(movieMapper.toMovieResponse(movie)).thenReturn(movieResponse);

            List<MovieResponse> result = movieCategoryService.getMoviesOfCategory(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).title()).isEqualTo("Inception");

            verify(categoryRepository, times(1)).existsById(1L);
            verify(movieCategoryRepository, times(1)).findByCategoryId(1L);
            verify(movieMapper, times(1)).toMovieResponse(movie);
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void getMoviesOfCategory_WithNonExistingCategory_ShouldThrowException() {
            when(categoryRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> movieCategoryService.getMoviesOfCategory(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Category not found");

            verify(categoryRepository, times(1)).existsById(999L);
            verify(movieCategoryRepository, never()).findByCategoryId(any());
        }
    }
}
