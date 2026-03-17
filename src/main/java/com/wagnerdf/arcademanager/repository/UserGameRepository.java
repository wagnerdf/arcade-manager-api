package com.wagnerdf.arcademanager.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.UserGame;

public interface UserGameRepository extends MongoRepository<UserGame, String> {

    List<UserGame> findByUserId(String userId);
    
    Page<UserGame> findByUserId(String userId, Pageable pageable);

}
