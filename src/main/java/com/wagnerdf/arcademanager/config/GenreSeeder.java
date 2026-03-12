package com.wagnerdf.arcademanager.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.repository.GenreRepository;

@Configuration
public class GenreSeeder {

    @Bean
    CommandLineRunner seedGenres(GenreRepository genreRepository) {
        return args -> {

            if (genreRepository.count() > 0) {
                return;
            }

            List<Genre> genres = List.of(
                    Genre.builder().name("Action").build(),
                    Genre.builder().name("Adventure").build(),
                    Genre.builder().name("RPG").build(),
                    Genre.builder().name("Shooter").build(),
                    Genre.builder().name("Racing").build(),
                    Genre.builder().name("Strategy").build(),
                    Genre.builder().name("Fighting").build(),
                    Genre.builder().name("Sports").build(),
                    Genre.builder().name("Simulation").build(),
                    Genre.builder().name("Puzzle").build()
            );

            genreRepository.saveAll(genres);

            System.out.println("Genres seeded successfully.");
        };
    }
}