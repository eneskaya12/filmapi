package org.example.filmapi.contorller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.model.dto.request.CategoryCreateRequest;
import org.example.filmapi.model.dto.request.CategoryUpdateRequest;
import org.example.filmapi.model.dto.response.CategoryResponse;
import org.example.filmapi.model.dto.response.GenericResponse;
import org.example.filmapi.model.dto.response.PagedResponse;
import org.example.filmapi.service.CategoryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management operations")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Add a new category",
            description = "Creates a new category. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PostMapping
    public GenericResponse<Void> addCategory(@RequestBody @Valid CategoryCreateRequest request) {
        categoryService.addCategory(request);
        return new GenericResponse<>(true, "Category added successfully", null);
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
    public GenericResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return new GenericResponse<>(true, "Category details retrieved successfully", response);
    }

    @Operation(
            summary = "Get all categories",
            description = "Returns a paginated list of all categories. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping
    public GenericResponse<PagedResponse<CategoryResponse>> getAllCategories(
            @ParameterObject @PageableDefault(page = 0, size = 5) Pageable pageable) {
        PagedResponse<CategoryResponse> response = categoryService.getAllCategories(pageable);
        return new GenericResponse<>(true, "Categories retrieved successfully", response);
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
    public GenericResponse<Void> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryUpdateRequest request) {
        categoryService.updateCategory(id, request);
        return new GenericResponse<>(true, "Category updated successfully", null);
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
    public GenericResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new GenericResponse<>(true, "Category deleted successfully", null);
    }
}
