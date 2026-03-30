package com.wagnerdf.arcademanager.controller;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wagnerdf.arcademanager.dto.AddUserGameRequest;
import com.wagnerdf.arcademanager.dto.UpdateUserGameRequest;
import com.wagnerdf.arcademanager.dto.UserGameResponse;
import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.entity.UserGame;
import com.wagnerdf.arcademanager.repository.UserRepository;
import com.wagnerdf.arcademanager.service.UserGameService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-games")
@RequiredArgsConstructor
public class UserGameController {

    private final UserGameService userGameService;
    
    private final UserRepository userRepository;

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
    public ResponseEntity<Void> addGame(@RequestBody AddUserGameRequest request) {

        userGameService.addGameToUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    
    /**
     * Atualiza informações de um jogo da biblioteca do usuário autenticado.
     *
     * Endpoint:
     * PUT /api/user-games/{id}
     *
     * Permite atualizar:
     * - status
     * - mediaType
     *
     * Regras:
     * - O jogo deve existir
     * - Deve pertencer ao usuário autenticado
     *
     * @param id ID do UserGame
     * @param request Dados para atualização
     * @param authentication Usuário autenticado
     * @return UserGame atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserGame> update(
            @PathVariable String id,
            @RequestBody UpdateUserGameRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserGame updated = userGameService.update(id, user.getId(), request);

        return ResponseEntity.ok(updated);
    }
    
    /**
     * Remove um jogo da biblioteca do usuário autenticado.
     *
     * Endpoint:
     * DELETE /api/user-games/{id}
     *
     * Regras:
     * - O jogo deve existir
     * - Deve pertencer ao usuário autenticado
     *
     * @param id ID do UserGame
     * @param authentication Usuário autenticado
     * @return 204 No Content em caso de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userGameService.delete(id, user.getId());

        return ResponseEntity.noContent().build();
    }    
}
