package com.wagnerdf.arcademanager.service;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wagnerdf.arcademanager.dto.AddUserGameRequest;
import com.wagnerdf.arcademanager.dto.RawgGameDTO;
import com.wagnerdf.arcademanager.dto.UpdateUserGameRequest;
import com.wagnerdf.arcademanager.dto.UserDashboardResponse;
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
                    .statusDescription(userGame.getStatus().getDescription())
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
    @Transactional
    public void delete(String id, String userId) {

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        // 🔒 Validação se pertence ao usuário
        if (!userGame.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }

        userGameRepository.delete(userGame);
    }
    
    /**
     * Adiciona um jogo à biblioteca do usuário autenticado.
     *
     * Regras:
     * - Busca o jogo pelo externalId no banco local
     * - Caso não exista, consulta a API externa (RAWG) e salva o jogo
     * - Usuário deve estar autenticado
     * - Não permite adicionar o mesmo jogo duas vezes para o mesmo usuário
     * - Caso o status não seja informado, define como BACKLOG por padrão
     * 
     * Fluxo:
     * 1. Busca ou cria o jogo
     * 2. Recupera o usuário autenticado
     * 3. Verifica duplicidade na biblioteca
     * 4. Cria o vínculo UserGame
     * 5. Salva no banco
     *
     * @param request dados do jogo a ser adicionado à biblioteca do usuário
     */
    @Transactional
    public UserGameResponse addGameToUser(AddUserGameRequest request) {

    	// 🔹 1. Buscar ou criar Game usando externalId
    	Game game = gameRepository.findByExternalId(request.getExternalId())
            .orElseGet(() -> {
                RawgGameDTO rawgGame = rawgService.getGameById(request.getExternalId());
                return saveNewGame(rawgGame);
            });

    	// 🔹 2. Usuário autenticado
    	User user = userService.getAuthenticatedUser();

    	// 🔹 3. Evitar duplicidade
        boolean exists = userGameRepository.existsByUserIdAndGameId(user.getId(), game.getId());
        if (exists) {
            throw new RuntimeException("Jogo já está na sua biblioteca");
        }

        // 🔹 4. Definir status padrão
        GameStatus status = request.getStatus() != null
                ? request.getStatus()
                : GameStatus.BACKLOG;

        // 🔹 5. Criar UserGame
        UserGame userGame = new UserGame();
        userGame.setUserId(user.getId());
        userGame.setGameId(game.getId());
        userGame.setStatus(status);
        userGame.setMediaType(request.getMediaType());

        // 🔹 6. Salvar no banco
        UserGame saved = userGameRepository.save(userGame);
        
        // 🔥 7. RETORNAR DTO (AQUI QUE ENTRA O TRECHO QUE VOCÊ FICOU EM DÚVIDA)
        return UserGameResponse.builder()
            .id(saved.getId())
            .gameTitle(game.getTitle())
            .platforms(game.getPlatforms())
            .genres(game.getGenres())
            .externalGameId(game.getExternalId())
            .mediaType(saved.getMediaType())
            .status(saved.getStatus())
            .statusDescription(saved.getStatus().getDescription())
            .build();
    }
       
    /**
     * Salva um novo jogo no banco de dados a partir dos dados
     * retornados pela API externa (RAWG).
     *
     * @param dto dados do jogo obtidos da API externa
     * @return entidade Game persistida no banco
     */
    private Game saveNewGame(RawgGameDTO dto) {

        Game game = new Game();
        game.setExternalId(dto.getExternalId());
        game.setTitle(dto.getName());
        game.setCoverUrl(dto.getBackgroundImage());

        return gameRepository.save(game);
    }
    
    /**
     * Atualiza o status de um jogo na biblioteca do usuário.
     *
     * Regras de negócio:
     * - O registro UserGame deve existir na base de dados.
     * - Apenas o usuário dono do registro pode atualizar o status.
     * - (Opcional) Pode validar transições de status inválidas.
     *
     * @param userGameId ID do registro UserGame a ser atualizado
     * @param newStatus novo status a ser atribuído ao jogo
     * @param userId ID do usuário autenticado (referência ao campo userId do UserGame)
     *
     * @throws BusinessException com status NOT_FOUND caso o UserGame não seja encontrado
     * @throws BusinessException com status FORBIDDEN caso o usuário não seja o dono do registro
     * @throws BusinessException com status BAD_REQUEST caso a transição de status seja inválida (se implementado)
     */
    @Transactional
    public void updateGameStatus(String userGameId, GameStatus newStatus, String userId) {

    	UserGame userGame = userGameRepository.findById(userGameId)
    			.orElseThrow(() -> new BusinessException(
    				    "Jogo de usuário não encontrado com o ID especificado.: " + userGameId,
    				    HttpStatus.NOT_FOUND
    				));

    	// valida dono
    	if (!userGame.getUserId().equals(userId)) {
    	    throw new BusinessException(
    	        "Você não tem permissão para atualizar este jogo.",
    	        HttpStatus.FORBIDDEN
    	    );
    	}

        // validar transição
        GameStatus currentStatus = userGame.getStatus();

        if (currentStatus == GameStatus.COMPLETED && newStatus == GameStatus.PLAYING) {
            throw new IllegalStateException("Não é possivel voltar de COMPLETED para PLAYING");
        }

        userGame.setStatus(newStatus);
        userGameRepository.save(userGame);
    }
    
    /**
     * Retorna o resumo da biblioteca de jogos de um usuário.
     *
     * Regras de negócio:
     * - Busca todos os jogos associados ao usuário
     * - Calcula o total de jogos
     * - Agrupa os jogos por status (COMPLETED, PLAYING, BACKLOG, WISHLIST)
     *
     * @param userId ID do usuário autenticado
     *
     * @return objeto contendo as estatísticas da biblioteca do usuário
     */
    public UserDashboardResponse getUserDashboard(String userId) {

        List<UserGame> userGames = userGameRepository.findByUserId(userId);

        long total = userGames.size();

        long completed = userGames.stream()
            .filter(g -> g.getStatus() == GameStatus.COMPLETED)
            .count();

        long playing = userGames.stream()
            .filter(g -> g.getStatus() == GameStatus.PLAYING)
            .count();

        long backlog = userGames.stream()
            .filter(g -> g.getStatus() == GameStatus.BACKLOG)
            .count();

        long wishlist = userGames.stream()
            .filter(g -> g.getStatus() == GameStatus.WISHLIST)
            .count();

        return UserDashboardResponse.builder()
            .totalGames(total)
            .completed(completed)
            .playing(playing)
            .backlog(backlog)
            .wishlist(wishlist)
            .build();
    }
    
}
