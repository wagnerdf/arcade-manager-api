package com.wagnerdf.arcademanager.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.UserGame;

public interface UserGameRepository extends MongoRepository<UserGame, String> {

    List<UserGame> findByUserId(String userId);

}
