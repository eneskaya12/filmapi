package org.example.cinecore.repository;

import org.example.cinecore.model.entity.MovieUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieUserStatusRepository extends JpaRepository<MovieUserStatus, Long> {
    Optional<MovieUserStatus> findByUserIdAndMovieId(Long userId, Long movieId);
    List<MovieUserStatus> findByUserId(Long userId);
    List<MovieUserStatus> findByUserIdAndIsFavoriteTrue(Long userId);
    List<MovieUserStatus> findByUserIdAndIsWatchedTrue(Long userId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
