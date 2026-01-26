package org.example.cinecore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.GenericResponse;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.service.MovieCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Movie-Category Relations", description = "Movie and category relationship operations")
public class MovieCategoryController {
    private final MovieCategoryService movieCategoryService;

    @Operation(
            summary = "Add category to movie",
            description = "Assigns a category to a movie. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category added to movie successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Movie or category not found"),
            @ApiResponse(responseCode = "409", description = "Category already assigned to movie")
    })
    @PostMapping("/api/movies/{movieId}/categories/{categoryId}")
    public GenericResponse<Void> addCategoryToMovie(
            @PathVariable Long movieId,
            @PathVariable Long categoryId) {
        movieCategoryService.addCategoryToMovie(movieId, categoryId);
        return new GenericResponse<>(true, "Category added to movie successfully", null);
    }

    @Operation(
            summary = "Remove category from movie",
            description = "Removes a category from a movie. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category removed from movie successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Movie-Category relation not found")
    })
    @DeleteMapping("/api/movies/{movieId}/categories/{categoryId}")
    public GenericResponse<Void> removeCategoryFromMovie(
            @PathVariable Long movieId,
            @PathVariable Long categoryId) {
        movieCategoryService.removeCategoryFromMovie(movieId, categoryId);
        return new GenericResponse<>(true, "Category removed from movie successfully", null);
    }

    @Operation(
            summary = "Get categories of a movie",
            description = "Returns all categories assigned to a movie. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/api/movies/{movieId}/categories")
    public GenericResponse<List<CategoryResponse>> getCategoriesOfMovie(@PathVariable Long movieId) {
        List<CategoryResponse> response = movieCategoryService.getCategoriesOfMovie(movieId);
        return new GenericResponse<>(true, "Categories retrieved successfully", response);
    }

    @Operation(
            summary = "Get movies of a category",
            description = "Returns all movies in a category. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/api/categories/{categoryId}/movies")
    public GenericResponse<List<MovieResponse>> getMoviesOfCategory(@PathVariable Long categoryId) {
        List<MovieResponse> response = movieCategoryService.getMoviesOfCategory(categoryId);
        return new GenericResponse<>(true, "Movies retrieved successfully", response);
    }
}
