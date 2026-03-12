package com.wagnerdf.arcademanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }
}