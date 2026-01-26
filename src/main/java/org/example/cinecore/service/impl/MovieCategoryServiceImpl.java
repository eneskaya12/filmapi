package org.example.cinecore.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cinecore.exception.DuplicateNameException;
import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.CategoryMapper;
import org.example.cinecore.mapper.MovieMapper;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.entity.Category;
import org.example.cinecore.model.entity.Movie;
import org.example.cinecore.model.entity.MovieCategory;
import org.example.cinecore.repository.CategoryRepository;
import org.example.cinecore.repository.MovieCategoryRepository;
import org.example.cinecore.repository.MovieRepository;
import org.example.cinecore.service.MovieCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieCategoryServiceImpl implements MovieCategoryService {
    private final MovieCategoryRepository movieCategoryRepository;
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final MovieMapper movieMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void addCategoryToMovie(Long movieId, Long categoryId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        boolean exists = movieCategoryRepository.existsByMovieIdAndCategoryId(movieId, categoryId);
        if (exists) {
            throw new DuplicateNameException("This category is already assigned to the movie");
        }

        MovieCategory movieCategory = MovieCategory.builder()
                .movie(movie)
                .category(category)
                .build();

        movieCategoryRepository.save(movieCategory);
    }

    @Override
    @Transactional
    public void removeCategoryFromMovie(Long movieId, Long categoryId) {
        MovieCategory movieCategory = movieCategoryRepository.findByMovieIdAndCategoryId(movieId, categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Movie-Category relation not found"));

        movieCategoryRepository.delete(movieCategory);
    }

    @Override
    public List<CategoryResponse> getCategoriesOfMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new EntityNotFoundException("Movie not found");
        }

        return movieCategoryRepository.findByMovieId(movieId)
                .stream()
                .map(mc -> categoryMapper.toCategoryResponse(mc.getCategory()))
                .toList();
    }

    @Override
    public List<MovieResponse> getMoviesOfCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found");
        }

        return movieCategoryRepository.findByCategoryId(categoryId)
                .stream()
                .map(mc -> movieMapper.toMovieResponse(mc.getMovie()))
                .toList();
    }
}
