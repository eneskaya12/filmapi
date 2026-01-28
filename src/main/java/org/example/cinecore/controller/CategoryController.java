package org.example.cinecore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinecore.model.dto.request.CategoryCreateRequest;
import org.example.cinecore.model.dto.request.CategoryUpdateRequest;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.GenericResponse;
import org.example.cinecore.model.dto.response.MovieResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.example.cinecore.service.CategoryService;
import org.example.cinecore.service.MovieCategoryService;

import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management operations")
public class CategoryController {

    private final CategoryService categoryService;
    private final MovieCategoryService movieCategoryService;

    @Operation(
            summary = "Add a new category",
            description = "Creates a new category. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PostMapping
    public ResponseEntity<GenericResponse<Void>> addCategory(@RequestBody @Valid CategoryCreateRequest request) {
        categoryService.addCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Category added successfully", null));
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves category details by its ID. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Category details retrieved successfully", response));
    }

    @Operation(
            summary = "Get all categories",
            description = "Returns a paginated list of all categories. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<GenericResponse<PagedResponse<CategoryResponse>>> getAllCategories(
            @ParameterObject @PageableDefault(page = 0, size = 5) Pageable pageable) {
        PagedResponse<CategoryResponse> response = categoryService.getAllCategories(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Categories retrieved successfully", response));
    }

    @Operation(
            summary = "Update category",
            description = "Updates an existing category. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryUpdateRequest request) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Category updated successfully", null));
    }

    @Operation(
            summary = "Delete category",
            description = "Deletes a category by its ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Category deleted successfully", null));
    }

    @Operation(
            summary = "Get movies of a category",
            description = "Returns all movies in a category. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}/movies")
    public ResponseEntity<GenericResponse<List<MovieResponse>>> getMoviesOfCategory(@PathVariable Long id) {
        List<MovieResponse> response = movieCategoryService.getMoviesOfCategory(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Movies retrieved successfully", response));
    }
}
