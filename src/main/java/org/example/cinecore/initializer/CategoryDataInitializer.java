package org.example.cinecore.initializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.cinecore.model.entity.Category;
import org.example.cinecore.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(2)
public class CategoryDataInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    Category.builder().name("Aksiyon").build(),
                    Category.builder().name("Komedi").build(),
                    Category.builder().name("Dram").build(),
                    Category.builder().name("Korku").build(),
                    Category.builder().name("Bilim Kurgu").build(),
                    Category.builder().name("Romantik").build()
            );
            categoryRepository.saveAll(categories);
        }
    }
}
