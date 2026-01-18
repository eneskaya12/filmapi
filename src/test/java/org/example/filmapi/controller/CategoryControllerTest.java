package org.example.filmapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.filmapi.config.TestSecurityConfig;
import org.example.filmapi.model.dto.request.CategoryCreateRequest;
import org.example.filmapi.model.dto.request.CategoryUpdateRequest;
import org.example.filmapi.model.dto.response.CategoryResponse;
import org.example.filmapi.model.dto.response.PagedResponse;
import org.example.filmapi.security.JwtAuthenticationFilter;
import org.example.filmapi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@Import(TestSecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryResponse categoryResponse;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Action")
                .build();

        createRequest = new CategoryCreateRequest("Action");
        updateRequest = new CategoryUpdateRequest("Adventure");
    }

    @Nested
    @DisplayName("GET /api/categories - public endpoint tests")
    class PublicEndpointTests {

        @Test
        @DisplayName("anyone can list categories")
        void getAllCategories_WithoutAuth_ShouldReturn200() throws Exception {
            PagedResponse<CategoryResponse> pagedResponse = PagedResponse.<CategoryResponse>builder()
                    .content(List.of(categoryResponse))
                    .pageNumber(0)
                    .pageSize(5)
                    .totalElements(1)
                    .totalPages(1)
                    .last(true)
                    .build();

            when(categoryService.getAllCategories(any())).thenReturn(pagedResponse);

            mockMvc.perform(get("/api/categories").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Categories retrieved successfully"))
                    .andExpect(jsonPath("$.payload.content[0].name").value("Action"));

            verify(categoryService, times(1)).getAllCategories(any());
        }

        @Test
        @DisplayName("get category by id - anyone can list")
        void getCategoryById_WithoutAuth_ShouldReturn200() throws Exception {
            when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

            mockMvc.perform(get("/api/categories/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.id").value(1))
                    .andExpect(jsonPath("$.payload.name").value("Action"));

            verify(categoryService, times(1)).getCategoryById(1L);
        }
    }

    @Nested
    @DisplayName("POST/PATCH/DELETE - ADMIN required endpoint tests")
    class AdminEndpointTests {

        @Test
        @DisplayName("ADMIN user should add category")
        @WithMockUser(roles = "ADMIN")
        void addCategory_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(categoryService).addCategory(any(CategoryCreateRequest.class));

            mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Category added successfully"));

            verify(categoryService, times(1)).addCategory(any(CategoryCreateRequest.class));
        }

        @Test
        @DisplayName("Normal user should not add category - 403 Forbidden")
        @WithMockUser(roles = "USER")
        void addCategory_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isForbidden());

            verify(categoryService, never()).addCategory(any());
        }

        @Test
        @DisplayName("Unsigned user should not add movie - 403 Forbidden")
        void addCategory_WithoutAuth_ShouldReturn403() throws Exception {
            mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isForbidden());

            verify(categoryService, never()).addCategory(any());
        }

        @Test
        @DisplayName("ADMIN user should update category")
        @WithMockUser(roles = "ADMIN")
        void updateCategory_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(categoryService).updateCategory(eq(1L), any(CategoryUpdateRequest.class));

            mockMvc.perform(patch("/api/categories/1").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category updated successfully"));

            verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryUpdateRequest.class));
        }

        @Test
        @DisplayName("ADMIN user should delete category")
        @WithMockUser(roles = "ADMIN")
        void deleteCategory_WithAdminRole_ShouldReturn200() throws Exception {
            doNothing().when(categoryService).deleteCategory(1L);

            mockMvc.perform(delete("/api/categories/1")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category deleted successfully"));

            verify(categoryService, times(1)).deleteCategory(1L);
        }

        @Test
        @DisplayName("Normal user should not delete category - 403 Forbidden")
        @WithMockUser(roles = "USER")
        void deleteCategory_WithUserRole_ShouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/categories/1"))
                    .andExpect(status().isForbidden());

            verify(categoryService, never()).deleteCategory(any());
        }
    }

    @Nested
    @DisplayName("Validation tests")
    class ValidationTests {

        @Test
        @DisplayName("Dont add category with blank name - 400 Bad Request")
        @WithMockUser(roles = "ADMIN")
        void addCategory_WithEmptyName_ShouldReturn400() throws Exception {
            CategoryCreateRequest invalidRequest = new CategoryCreateRequest("");

            mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(categoryService, never()).addCategory(any());
        }

        @Test
        @DisplayName("Dont add category with Null name - 400 Bad Request")
        @WithMockUser(roles = "ADMIN")
        void addCategory_WithNullName_ShouldReturn400() throws Exception {
            String invalidJson = "{}";

            mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(categoryService, never()).addCategory(any());
        }
    }
}