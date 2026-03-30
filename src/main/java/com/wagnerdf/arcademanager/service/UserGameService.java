package com.wagnerdf.arcademanager.service;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wagnerdf.arcademanager.dto.AddUserGameRequest;
import com.wagnerdf.arcademanager.dto.RawgGameDTO;
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
    private final RawgService rawgService;

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
    @Transactional
    public UserGameResponse addGameToLibrary(AddUserGameRequest request) {

        // 🔹 1. Buscar ou criar Game usando externalId
        Game game = gameRepository.findByExternalId(request.getExternalId())
                .orElseGet(() -> {
                    RawgGameDTO rawgGame = rawgService.getGameById(request.getExternalId());
                    return saveNewGame(rawgGame);
                });

        // 🔹 2. Usuário autenticado
        User user = userService.getAuthenticatedUser();

        // 🔹 3. Evitar duplicidade
        boolean alreadyExists = userGameRepository
                .existsByUserIdAndGameId(user.getId(), game.getId());

        if (alreadyExists) {
            throw new BusinessException("Game already in your library", HttpStatus.CONFLICT);
        }

        // 🔹 4. Criar UserGame
        UserGame userGame = UserGame.builder()
                .userId(user.getId())
                .gameId(game.getId())
                .externalGameId(game.getExternalId())
                .mediaType(request.getMediaType())
                .status(request.getStatus() != null ? request.getStatus() : GameStatus.BACKLOG)
                .build();

        UserGame saved = userGameRepository.save(userGame);

        // 🔹 5. Response
        return UserGameResponse.builder()
                .id(saved.getId())
                .gameTitle(game.getTitle())
                .platforms(game.getPlatforms())
                .genres(game.getGenres())
                .externalGameId(game.getExternalId())
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
                    .platforms(game.getPlatforms())
                    .genres(game.getGenres())
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
    @Transactional
    public UserGame update(String id, String userId, UpdateUserGameRequest request) {

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

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
    
    /**
     * Remove um jogo da biblioteca do usuário.
     *
     * Regras:
     * - O jogo deve existir
     * - O jogo deve pertencer ao usuário autenticado
     *
     * @param id ID do UserGame
     * @param userId ID do usuário autenticado
     */
    public void delete(String id, String userId) {

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        // 🔒 Validação de dono
        if (!userGame.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }

        userGameRepository.delete(userGame);
    }
    
    @Transactional
    public void addGameToUser(AddUserGameRequest request) {

    	Game game = gameRepository.findByExternalId(request.getExternalId())
            .orElseGet(() -> {
                RawgGameDTO rawgGame = rawgService.getGameById(request.getExternalId());
                return saveNewGame(rawgGame);
            });

    	User user = userService.getAuthenticatedUser();

        boolean exists = userGameRepository.existsByUserIdAndGameId(user.getId(), game.getId());
        if (exists) {
            throw new RuntimeException("Jogo já está na sua biblioteca");
        }

        GameStatus status = request.getStatus() != null
                ? request.getStatus()
                : GameStatus.BACKLOG;

        UserGame userGame = new UserGame();
        userGame.setUserId(user.getId());
        userGame.setGameId(game.getId());
        userGame.setStatus(status);
        userGame.setMediaType(request.getMediaType());

        userGameRepository.save(userGame);
    }
       
    private Game saveNewGame(RawgGameDTO dto) {

        Game game = new Game();
        game.setExternalId(dto.getExternalId());
        game.setTitle(dto.getName());
        game.setCoverUrl(dto.getBackgroundImage());

        return gameRepository.save(game);
    }
}
