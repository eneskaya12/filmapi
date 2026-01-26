package org.example.cinecore.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.CategoryMapper;
import org.example.cinecore.model.dto.request.CategoryCreateRequest;
import org.example.cinecore.model.dto.request.CategoryUpdateRequest;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.example.cinecore.model.entity.Category;
import org.example.cinecore.repository.CategoryRepository;
import org.example.cinecore.service.CategoryService;
import org.example.cinecore.validator.CategoryValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    @Override
    public void addCategory(CategoryCreateRequest request) {
        categoryValidator.validateCategoryUnique(request.name());
        Category category = categoryMapper.toCategory(request);
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = getCategory(id);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public PagedResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<CategoryResponse> page = categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryResponse);
        return PagedResponse.of(page);
    }

    @Override
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = getCategory(id);

        Optional.ofNullable(request.name())
                .filter(newName -> !newName.equals(category.getName()))
                .ifPresent(newName -> {
                    categoryValidator.validateCategoryUnique(newName);
                    category.setName(newName);
                });

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategory(id);
        categoryRepository.delete(category);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

}
