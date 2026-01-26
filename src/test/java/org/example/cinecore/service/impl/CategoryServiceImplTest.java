package org.example.cinecore.service.impl;

import org.example.cinecore.exception.EntityNotFoundException;
import org.example.cinecore.mapper.CategoryMapper;
import org.example.cinecore.model.dto.request.CategoryCreateRequest;
import org.example.cinecore.model.dto.request.CategoryUpdateRequest;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.dto.response.PagedResponse;
import org.example.cinecore.model.entity.Category;
import org.example.cinecore.repository.CategoryRepository;
import org.example.cinecore.validator.CategoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryValidator categoryValidator;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryResponse categoryResponse;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Action")
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Action")
                .build();

        createRequest = new CategoryCreateRequest("Action");

        updateRequest = new CategoryUpdateRequest("Adventure");
    }

    @Nested
    @DisplayName("addCategory method tests")
    class AddCategoryTests {

        @Test
        @DisplayName("add category successfully - all steps need to be done")
        void addCategory_WithValidRequest_ShouldSaveCategory() {
            doNothing().when(categoryValidator).validateCategoryUnique("Action");
            when(categoryMapper.toCategory(createRequest)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);

            categoryService.addCategory(createRequest);

            verify(categoryValidator, times(1)).validateCategoryUnique("Action");
            verify(categoryMapper, times(1)).toCategory(createRequest);
            verify(categoryRepository, times(1)).save(category);
        }
    }

    @Nested
    @DisplayName("getCategoryById method tests")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("get category with existing id")
        void getCategoryById_WithExistingId_ShouldReturnCategory() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            CategoryResponse result = categoryService.getCategoryById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Action");

            verify(categoryRepository, times(1)).findById(1L);
            verify(categoryMapper, times(1)).toCategoryResponse(category);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException with non-existing Id")
        void getCategoryById_WithNonExistingId_ShouldThrowException() {
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Category not found");

            verify(categoryRepository, times(1)).findById(999L);
            verify(categoryMapper, never()).toCategoryResponse(any());
        }
    }

    @Nested
    @DisplayName("getAllCategories method tests")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("should return paged all categories")
        void getAllCategories_ShouldReturnPagedResponse() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Category> categoryPage = new PageImpl<>(
                    List.of(category),
                    pageable,
                    1
            );

            when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
            when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            PagedResponse<CategoryResponse> result = categoryService.getAllCategories(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("Action");

            verify(categoryRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("updateCategory method tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("update with different name - should validate and save")
        void updateCategory_WithDifferentName_ShouldValidateAndSave() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            doNothing().when(categoryValidator).validateCategoryUnique("Adventure");
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            categoryService.updateCategory(1L, updateRequest);

            verify(categoryValidator, times(1)).validateCategoryUnique("Adventure");
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @DisplayName("update with same name - should skip validation")
        void updateCategory_WithSameName_ShouldSkipValidation() {
            CategoryUpdateRequest sameNameRequest = new CategoryUpdateRequest("Action");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            categoryService.updateCategory(1L, sameNameRequest);

            verify(categoryValidator, never()).validateCategoryUnique(any());
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @DisplayName("update with non-existing id - should throw exception")
        void updateCategory_WithNonExistingId_ShouldThrowException() {
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(999L, updateRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Category not found");

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCategory method tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("should delete existing category")
        void deleteCategory_WithExistingId_ShouldDelete() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            doNothing().when(categoryRepository).delete(category);

            categoryService.deleteCategory(1L);

            verify(categoryRepository, times(1)).findById(1L);
            verify(categoryRepository, times(1)).delete(category);
        }

        @Test
        @DisplayName("delete category with non-existing id - should throw exception")
        void deleteCategory_WithNonExistingId_ShouldThrowException() {
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Category not found");

            verify(categoryRepository, never()).delete(any());
        }
    }
}
