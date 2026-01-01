package org.example.filmapi.service;

import org.example.filmapi.model.dto.request.CategoryCreateRequest;
import org.example.filmapi.model.dto.request.CategoryUpdateRequest;
import org.example.filmapi.model.dto.response.CategoryResponse;
import org.example.filmapi.model.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    void addCategory(CategoryCreateRequest request);

    CategoryResponse getCategoryById(Long id);

    PagedResponse<CategoryResponse> getAllCategories(Pageable pageable);

    void updateCategory(Long id, CategoryUpdateRequest request);
    void deleteCategory(Long id);
}
