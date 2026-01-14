package org.example.filmapi.mapper;

import org.example.filmapi.model.dto.request.CategoryCreateRequest;
import org.example.filmapi.model.dto.response.CategoryResponse;
import org.example.filmapi.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryCreateRequest request);

    CategoryResponse toCategoryResponse(Category category);
}
