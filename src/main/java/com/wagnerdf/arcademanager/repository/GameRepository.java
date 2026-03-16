package com.wagnerdf.arcademanager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.Game;

public interface GameRepository extends MongoRepository<Game, String> {

    boolean existsByTitleIgnoreCase(String title);

}