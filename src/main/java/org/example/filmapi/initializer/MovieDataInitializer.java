package org.example.filmapi.initializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.model.entity.Movie;
import org.example.filmapi.model.enums.Language;
import org.example.filmapi.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(3)
public class MovieDataInitializer implements CommandLineRunner {
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (movieRepository.count() == 0) {
            List<Movie> movies = List.of(
                    Movie.builder()
                            .title("Inception")
                            .description("A thief who steals corporate secrets through dream-sharing technology.")
                            .duration(148)
                            .language(Language.EN)
                            .imdb(8.8)
                            .releaseDate(toInstant(2010, 7, 16))
                            .build(),
                    Movie.builder()
                            .title("The Dark Knight")
                            .description("Batman faces the Joker, a criminal mastermind who wants to plunge Gotham into anarchy.")
                            .duration(152)
                            .language(Language.EN)
                            .imdb(9.0)
                            .releaseDate(toInstant(2008, 7, 18))
                            .build(),
                    Movie.builder()
                            .title("Interstellar")
                            .description("A team of explorers travel through a wormhole in space.")
                            .duration(169)
                            .language(Language.EN)
                            .imdb(8.6)
                            .releaseDate(toInstant(2014, 11, 7))
                            .build(),
                    Movie.builder()
                            .title("Kelebeğin Rüyası")
                            .description("İki genç şairin hayatlarının son dönemini anlatan dram filmi.")
                            .duration(138)
                            .language(Language.TR)
                            .imdb(8.0)
                            .releaseDate(toInstant(2013, 3, 15))
                            .build(),
                    Movie.builder()
                            .title("Ayla")
                            .description("Kore Savaşı sırasında bir Türk askerinin küçük bir kızla kurduğu bağ.")
                            .duration(125)
                            .language(Language.TR)
                            .imdb(8.3)
                            .releaseDate(toInstant(2017, 10, 27))
                            .build(),
                    Movie.builder()
                            .title("The Conjuring")
                            .description("Paranormal investigators help a family terrorized by a dark presence.")
                            .duration(112)
                            .language(Language.EN)
                            .imdb(7.5)
                            .releaseDate(toInstant(2013, 7, 19))
                            .build()
            );
            movieRepository.saveAll(movies);
        }
    }

    private Instant toInstant(int year, int month, int day) {
        return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
