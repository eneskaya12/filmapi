package org.example.filmapi.service;

import org.example.filmapi.model.dto.response.CategoryResponse;
import org.example.filmapi.model.dto.response.MovieResponse;

import java.util.List;

public interface MovieCategoryService {
    void addCategoryToMovie(Long movieId, Long categoryId);
    void removeCategoryFromMovie(Long movieId, Long categoryId);
    List<CategoryResponse> getCategoriesOfMovie(Long movieId);
    List<MovieResponse> getMoviesOfCategory(Long categoryId);
}
