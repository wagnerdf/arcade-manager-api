package com.wagnerdf.arcademanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.CreateGenreRequest;
import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public Genre createGenre(CreateGenreRequest request) {

        if (genreRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Gênero já cadastrado", HttpStatus.CONFLICT);
        }

        Genre genre = Genre.builder()
                .name(request.getName())
                .build();

        return genreRepository.save(genre);
    }
}
