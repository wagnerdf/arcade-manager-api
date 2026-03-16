package com.wagnerdf.arcademanager.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.CreateGenreRequest;
import com.wagnerdf.arcademanager.dto.UpdateGenreRequest;
import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.GenreRepository;
import com.wagnerdf.arcademanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    /**
     * Cria um novo gênero no sistema.
     * 
     * Regras aplicadas:
     * - O nome do gênero deve ser único
     * - A verificação ignora diferença entre maiúsculas e minúsculas
     */
    public Genre createGenre(CreateGenreRequest request) {

        if (genreRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Gênero já cadastrado", HttpStatus.CONFLICT);
        }

        Genre genre = Genre.builder()
                .name(request.getName())
                .build();

        return genreRepository.save(genre);
    }
    
    /**
     * Retorna todos os gêneros cadastrados no sistema.
     * 
     * Utilizado principalmente para:
     * - seleção de gêneros favoritos do usuário
     * - associação de jogos a gêneros
     */
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
    
    /**
     * Atualiza o nome de um gênero existente.
     * 
     * Regras aplicadas:
     * - O gênero deve existir no sistema
     * - O novo nome não pode duplicar outro gênero já cadastrado
     */
    public Genre updateGenre(String id, UpdateGenreRequest request) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Gênero não encontrado", HttpStatus.NOT_FOUND));

        if (genreRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Já existe um gênero com esse nome", HttpStatus.CONFLICT);
        }

        genre.setName(request.getName());

        return genreRepository.save(genre);
    }
    
    /**
     * Remove um gênero do sistema.
     * 
     * Regras aplicadas:
     * - O gênero deve existir
     * - Não pode ser removido se estiver associado
     *   aos gêneros favoritos de algum usuário
     */
    public void deleteGenre(String id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Gênero não encontrado", HttpStatus.NOT_FOUND));

        boolean genreInUse = userRepository.existsByFavoriteGenres_Id(id);

        if (genreInUse) {
            throw new BusinessException(
                    "Não é possível excluir um gênero associado a usuários",
                    HttpStatus.CONFLICT
            );
        }

        genreRepository.delete(genre);
    }
}
