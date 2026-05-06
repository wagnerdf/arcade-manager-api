package com.wagnerdf.arcademanager.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.UserGame;
import com.wagnerdf.arcademanager.enums.GameStatus;

public interface UserGameRepository extends MongoRepository<UserGame, String> {

    List<UserGame> findByUserId(String userId);
    
    Page<UserGame> findByUserId(String userId, Pageable pageable);
    
    boolean existsByUserIdAndGameId(String userId, String gameId);
    
    Page<UserGame> findByUserIdAndStatus(String userId, GameStatus status, Pageable pageable);
    
    long countByUserId(String userId);
    long countByUserIdAndStatus(String userId, GameStatus status);
    
    Optional<UserGame> findByIdAndUserId(String id, String userId);

}
