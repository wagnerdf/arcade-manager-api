package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.service.GenreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.findAll();
    }
}