package com.wagnerdf.arcademanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.AddUserGameRequest;
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
}
