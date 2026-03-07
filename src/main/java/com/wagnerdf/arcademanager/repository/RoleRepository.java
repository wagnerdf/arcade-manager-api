package com.wagnerdf.arcademanager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.entity.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(String name);

}