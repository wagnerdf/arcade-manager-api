package com.wagnerdf.arcademanager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.Platform;

public interface PlatformRepository extends MongoRepository<Platform, String> {

    boolean existsByNameIgnoreCase(String name);

}
