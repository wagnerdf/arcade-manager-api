package com.wagnerdf.arcademanager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.Game;

public interface GameRepository extends MongoRepository<Game, String> {

    boolean existsByTitleIgnoreCase(String title);
    
    Optional<Game> findByExternalId(Long externalId);

}