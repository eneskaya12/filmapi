package org.example.filmapi.initializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.model.entity.Category;
import org.example.filmapi.model.entity.Movie;
import org.example.filmapi.model.entity.MovieCategory;
import org.example.filmapi.repository.CategoryRepository;
import org.example.filmapi.repository.MovieCategoryRepository;
import org.example.filmapi.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(4)
public class MovieCategoryDataInitializer implements CommandLineRunner {
    private final MovieCategoryRepository movieCategoryRepository;
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (movieCategoryRepository.count() == 0) {
            List<MovieCategory> movieCategories = new ArrayList<>();

            addMovieCategories(movieCategories, "Inception", "Bilim Kurgu", "Aksiyon");
            addMovieCategories(movieCategories, "The Dark Knight", "Aksiyon", "Dram");
            addMovieCategories(movieCategories, "Interstellar", "Bilim Kurgu", "Dram");
            addMovieCategories(movieCategories, "Kelebeğin Rüyası", "Dram", "Romantik");
            addMovieCategories(movieCategories, "Ayla", "Dram");
            addMovieCategories(movieCategories, "The Conjuring", "Korku");

            movieCategoryRepository.saveAll(movieCategories);
        }
    }

    private void addMovieCategories(List<MovieCategory> list, String movieTitle, String... categoryNames) {
        Movie movie = movieRepository.findByTitle(movieTitle).orElse(null);
        if (movie == null) return;

        for (String categoryName : categoryNames) {
            Category category = categoryRepository.findByName(categoryName).orElse(null);
            if (category != null) {
                list.add(MovieCategory.builder()
                        .movie(movie)
                        .category(category)
                        .build());
            }
        }
    }
}
