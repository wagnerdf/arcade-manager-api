package com.wagnerdf.arcademanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}