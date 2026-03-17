package com.wagnerdf.arcademanager.controller;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wagnerdf.arcademanager.dto.AddUserGameRequest;
import com.wagnerdf.arcademanager.dto.UserGameResponse;
import com.wagnerdf.arcademanager.service.UserGameService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserGameController {

    private final UserGameService userGameService;

    /**
     * Adiciona um jogo à biblioteca do usuário autenticado.
     *
     * Fluxo:
     * 1. Recebe gameId + dados do usuário
     * 2. Associa ao usuário logado
     * 3. Salva na coleção user_games
     *
     * @param request dados do jogo a ser adicionado
     * @return UserGameResponse com dados do jogo salvo
     */
    @PostMapping("/me/library")
    public ResponseEntity<UserGameResponse> addGame(@Valid @RequestBody AddUserGameRequest request) {
        UserGameResponse response = userGameService.addGameToLibrary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Adiciona um jogo à biblioteca do usuário autenticado.
     *
     * Fluxo:
     * 1. Recebe gameId + dados do usuário
     * 2. Associa ao usuário logado
     * 3. Salva na coleção user_games
     *
     * @param request dados do jogo a ser adicionado
     * @return UserGameResponse com dados do jogo salvo
     */
    @GetMapping("/me/library")
    public ResponseEntity<Page<UserGameResponse>> getLibrary(Pageable pageable) {
        Page<UserGameResponse> library = userGameService.getUserLibrary(pageable);
        return ResponseEntity.ok(library);
    }
}
