package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wagnerdf.arcademanager.dto.RawgGameDTO;
import com.wagnerdf.arcademanager.service.RawgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final RawgService rawgService;

    /**
     * Realiza a busca de jogos utilizando a API externa RAWG.
     *
     * Este endpoint permite pesquisar jogos pelo nome e opcionalmente
     * filtrar por plataforma (ex: PC, PlayStation, Xbox).
     *
     * Fluxo:
     * 1. Recebe os parâmetros de busca (name e platform)
     * 2. Chama o RawgService para consultar a API externa
     * 3. Retorna a lista de jogos encontrados
     *
     * @param name Nome do jogo a ser pesquisado (obrigatório)
     * @param platform Plataforma para refinar a busca (opcional)
     * @return Lista de jogos encontrados convertidos em DTO
     */
    @GetMapping("/search")
    public ResponseEntity<List<RawgGameDTO>> search(
            @RequestParam String name,
            @RequestParam(required = false) String platform
    ) {

        List<RawgGameDTO> games = rawgService.searchGames(name, platform);

        return ResponseEntity.ok(games);
    }
}