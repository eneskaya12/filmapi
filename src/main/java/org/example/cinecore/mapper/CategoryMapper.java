package org.example.cinecore.mapper;

import org.example.cinecore.model.dto.request.CategoryCreateRequest;
import org.example.cinecore.model.dto.response.CategoryResponse;
import org.example.cinecore.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryCreateRequest request);

    CategoryResponse toCategoryResponse(Category category);
}
