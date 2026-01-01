package org.example.filmapi.repository;

import org.example.filmapi.model.entity.MovieCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieCategoryRepository extends JpaRepository<MovieCategory, Long> {
    List<MovieCategory> findByMovieId(Long movieId);
    List<MovieCategory> findByCategoryId(Long categoryId);
    Optional<MovieCategory> findByMovieIdAndCategoryId(Long movieId, Long categoryId);
    boolean existsByMovieIdAndCategoryId(Long movieId, Long categoryId);
}
