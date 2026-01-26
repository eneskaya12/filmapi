package org.example.cinecore.service;

import org.example.cinecore.model.dto.request.CategoryCreateRequest;
import org.example.cinecore.model.dto.request.CategoryUpdateRequest;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    void addCategory(CategoryCreateRequest request);

    CategoryResponse getCategoryById(Long id);

    PagedResponse<CategoryResponse> getAllCategories(Pageable pageable);

    void updateCategory(Long id, CategoryUpdateRequest request);
    void deleteCategory(Long id);
}
