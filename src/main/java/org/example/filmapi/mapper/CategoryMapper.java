package org.example.filmapi.mapper;

import org.example.filmapi.model.dto.request.CategoryCreateRequest;
import org.example.filmapi.model.dto.response.CategoryResponse;
import org.example.filmapi.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toCategory(CategoryCreateRequest request){
        return Category.builder()
                .name(request.name())
                .build();
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
