package com.wagnerdf.arcademanager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wagnerdf.arcademanager.enums.Role;
import com.wagnerdf.arcademanager.entity.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    
    /**
     * Pergunta ao Mongo se existe algum usuário usando um referido gênero
     **/
    boolean existsByFavoriteGenres_Id(String genreId);

}
