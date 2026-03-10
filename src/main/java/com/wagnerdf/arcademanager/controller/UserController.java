package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para registrar um novo usuário
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User userRequest) {
        User createdUser = userService.registerUser(userRequest);
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
    public ResponseEntity<User> promoteUser(@PathVariable String id) {

        User updatedUser = userService.promoteToAdmin(id);

        return ResponseEntity.ok(updatedUser);
    }
}