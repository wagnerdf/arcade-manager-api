package com.wagnerdf.arcademanager.service;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.AddUserGameRequest;
import com.wagnerdf.arcademanager.dto.UpdateUserGameRequest;
import com.wagnerdf.arcademanager.dto.UserGameResponse;
import com.wagnerdf.arcademanager.entity.Game;
import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.entity.UserGame;
import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.GameRepository;
import com.wagnerdf.arcademanager.repository.UserGameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserGameService {

    private final UserGameRepository userGameRepository;
    private final GameRepository gameRepository;
    private final UserService userService;

    /**
     * Adiciona um jogo à biblioteca do usuário autenticado.
     *
     * Regras:
     * - O jogo deve existir na base (games)
     * - O usuário é obtido via contexto de autenticação
     * - Status padrão é BACKLOG caso não informado
     *
     * @param request dados do jogo
     * @return UserGameResponse com dados formatados
     */
    public UserGameResponse addGameToLibrary(AddUserGameRequest request) {

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new BusinessException("Game not found", HttpStatus.NOT_FOUND));

        User user = userService.getAuthenticatedUser();

        UserGame userGame = new UserGame();
        userGame.setUserId(user.getId());
        userGame.setGameId(game.getId());
        userGame.setMediaType(request.getMediaType());
        userGame.setStatus(
                request.getStatus() != null ? request.getStatus() : GameStatus.BACKLOG
        );

        UserGame saved = userGameRepository.save(userGame);

        return UserGameResponse.builder()
                .id(saved.getId())
                .gameTitle(game.getTitle())
                .platform(game.getPlatform().getName())
                .mediaType(saved.getMediaType())
                .status(saved.getStatus())
                .build();
    }
    
    /**
     * Retorna a biblioteca de jogos do usuário autenticado com paginação.
     *
     * Fluxo:
     * 1. Busca usuário autenticado
     * 2. Busca registros em user_games por userId
     * 3. Para cada registro, busca dados do game
     * 4. Converte para DTO de resposta
     *
     * @param pageable parâmetros de paginação
     * @return página de UserGameResponse
     */    
    public Page<UserGameResponse> getUserLibrary(Pageable pageable) {

        User user = userService.getAuthenticatedUser();

        Page<UserGame> userGamesPage = userGameRepository.findByUserId(user.getId(), pageable);

        return userGamesPage.map(userGame -> {

            Game game = gameRepository.findById(userGame.getGameId())
                    .orElseThrow(() -> new BusinessException("Game not found", HttpStatus.NOT_FOUND));

            return UserGameResponse.builder()
                    .id(userGame.getId())
                    .gameTitle(game.getTitle())
                    .platform(game.getPlatform().getName())
                    .mediaType(userGame.getMediaType())
                    .status(userGame.getStatus())
                    .build();
        });
    }
    
    /**
     * Atualiza os dados de um jogo da biblioteca do usuário.
     *
     * Endpoint permite atualização parcial:
     * - status
     * - mediaType
     *
     * Regras:
     * - O jogo deve existir
     * - Deve pertencer ao usuário autenticado
     *
     * @param id ID do UserGame
     * @param userId ID do usuário autenticado
     * @param request Dados para atualização
     * @return UserGame atualizado
     */
    public UserGame update(String id, String userId, UpdateUserGameRequest request) {

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
        
        System.out.println("userGame.userId: " + userGame.getUserId());
        System.out.println("auth userId: " + userId);

        if (!userGame.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }

        if (request.getStatus() != null) {
            userGame.setStatus(request.getStatus());
        }

        if (request.getMediaType() != null) {
            userGame.setMediaType(request.getMediaType());
        }

        return userGameRepository.save(userGame);
    }
}
