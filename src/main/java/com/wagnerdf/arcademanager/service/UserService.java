package com.wagnerdf.arcademanager.service;

import java.util.List;

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
                .role(Role.USER)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Listar todos os usuários
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Promover usuário para ADMIN
     */
    public User promoteToAdmin(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        user.setRole(Role.ADMIN);

        return userRepository.save(user);
    }
    
    /**
     * Demote ADMIN to USER
     */
    public User demoteToUser(String userId, String currentAdminId) {

        if(userId.equals(currentAdminId)) {
            throw new BusinessException(
                "Você não pode remover seu próprio papel ADMIN", 
                HttpStatus.FORBIDDEN
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        // Verifica se já é USER
        if(user.getRole() == Role.USER) {
            throw new BusinessException(
                "Operação não realizada: usuário já é USER", 
                HttpStatus.BAD_REQUEST
            );
        }

        // Se não, faz o demote
        user.setRole(Role.USER);
        return userRepository.save(user);
    }
}