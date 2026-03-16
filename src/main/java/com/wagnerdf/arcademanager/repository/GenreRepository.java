package com.wagnerdf.arcademanager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.Genre;

public interface GenreRepository extends MongoRepository<Genre, String> {
	
	 boolean existsByNameIgnoreCase(String name);
	
}
