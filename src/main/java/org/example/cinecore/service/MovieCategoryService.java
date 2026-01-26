package org.example.cinecore.service;

import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.MovieResponse;

import java.util.List;

public interface MovieCategoryService {
    void addCategoryToMovie(Long movieId, Long categoryId);
    void removeCategoryFromMovie(Long movieId, Long categoryId);
    List<CategoryResponse> getCategoriesOfMovie(Long movieId);
    List<MovieResponse> getMoviesOfCategory(Long categoryId);
}
