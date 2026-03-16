package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.CreateGenreRequest;
import com.wagnerdf.arcademanager.dto.UpdateGenreRequest;
import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.service.GenreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    /**
     * Criar novo gênero
     * Apenas ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Genre> createGenre(@RequestBody CreateGenreRequest request) {

        Genre genre = genreService.createGenre(request);

        return new ResponseEntity<>(genre, HttpStatus.CREATED);
    }
    
    /**
     * Listar todos os gêneros cadastrados no sistema.
     * 
     * Endpoint utilizado para popular seletores de gênero
     * no cadastro ou edição de preferências do usuário.
     */
    @GetMapping
    public ResponseEntity<List<Genre>> getGenres() {

        List<Genre> genres = genreService.getAllGenres();

        return ResponseEntity.ok(genres);
    }
    
    /**
     * Atualizar o nome de um gênero existente.
     * Apenas ADMIN pode realizar esta operação.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Genre> updateGenre(
            @PathVariable String id,
            @RequestBody UpdateGenreRequest request) {

        Genre updatedGenre = genreService.updateGenre(id, request);

        return ResponseEntity.ok(updatedGenre);
    }
    
    /**
     * Remove um gênero do sistema.
     * Apenas ADMIN pode realizar esta operação.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGenre(@PathVariable String id) {

        genreService.deleteGenre(id);

        return ResponseEntity.noContent().build();
    }
}