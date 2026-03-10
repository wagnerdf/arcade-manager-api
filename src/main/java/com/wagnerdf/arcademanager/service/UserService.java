package com.wagnerdf.arcademanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.enums.Role;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User userRequest) {

        String email = userRequest.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .fullName(userRequest.getFullName())
                .email(email)
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(Role.USER)   // define enum USER por padrão
                .active(true)
                .build();

        return userRepository.save(user);
    }
}