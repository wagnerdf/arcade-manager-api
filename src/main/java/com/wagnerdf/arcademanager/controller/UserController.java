package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.RegisterUserRequest;
import com.wagnerdf.arcademanager.dto.UpdateUserProfileRequest;
import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.UserRepository;
import com.wagnerdf.arcademanager.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    private final UserRepository userRepository;

    /**
     * Endpoint para registrar um novo usuário
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserRequest request) {

        User createdUser = userService.registerUser(request);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Listar todos os usuários (somente ADMIN)
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Promover usuário para ADMIN
     * PUT /api/users/{id}/promote
     */
    @PutMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> promoteUser(@PathVariable String id, Authentication auth) {

        // Pega o email do token
        String email = auth.getName();

        // Busca no MongoDB o usuário logado
        User currentAdmin = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("Usuário logado não encontrado", HttpStatus.NOT_FOUND));

        User updatedUser = userService.promoteToAdmin(id, currentAdmin.getId());
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Reverter usuário para USER
     * PUT /api/users/{id}/demote
     */
    @PutMapping("/{id}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> demoteUser(@PathVariable String id, Authentication auth) {

        // Pega o email do usuário logado do token
        String email = auth.getName();

        // Busca o usuário no Mongo
        User currentAdmin = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("Usuário logado não encontrado", HttpStatus.NOT_FOUND));

        User updatedUser = userService.demoteToUser(id, currentAdmin.getId());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Habilitar ou desabilitar usuário
     * PUT api / users / {id} /status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUserStatus(@PathVariable String id) {

        User updatedUser = userService.toggleUserStatus(id);

        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Deletar usuário
     * DELETE api / users / {id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        return userService.findByEmail(email);
    }
    
    /**
    * Editar usuário
    * EDITAR api / users / {id}
    */
    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @RequestBody UpdateUserProfileRequest request,
            Authentication authentication) {

        User updatedUser = userService.updateUserProfile(authentication.getName(), request);

        return ResponseEntity.ok(updatedUser);
    }
}