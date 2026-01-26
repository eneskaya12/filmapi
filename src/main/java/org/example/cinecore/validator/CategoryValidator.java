package org.example.cinecore.validator;

import lombok.RequiredArgsConstructor;
import org.example.cinecore.exception.DuplicateNameException;
import org.example.cinecore.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {
    private final CategoryRepository categoryRepository;

    public void validateCategoryUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateNameException("A category with this name already exits");
        }
    }
}
