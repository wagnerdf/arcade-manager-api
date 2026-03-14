package com.wagnerdf.arcademanager.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.GenreResponse;
import com.wagnerdf.arcademanager.dto.RegisterUserRequest;
import com.wagnerdf.arcademanager.dto.UpdateUserProfileRequest;
import com.wagnerdf.arcademanager.dto.UserResponse;
import com.wagnerdf.arcademanager.entity.Genre;
import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.enums.Role;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.GenreRepository;
import com.wagnerdf.arcademanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;

    public User registerUser(RegisterUserRequest request) {

        String email = request.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
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
    public User promoteToAdmin(String userId, String currentAdminId) {

        // Impede que o ADMIN tente promover a si mesmo
        if(userId.equals(currentAdminId)) {
            throw new BusinessException(
                "Você não pode alterar seu próprio papel ADMIN", 
                HttpStatus.FORBIDDEN
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        // Verifica se já é ADMIN
        if(user.getRole() == Role.ADMIN) {
            throw new BusinessException(
                "Operação não realizada: usuário já é ADMIN", 
                HttpStatus.BAD_REQUEST
            );
        }

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
    
    public User toggleUserStatus(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedUserEmail = authentication.getName();

        if (user.getEmail().equals(loggedUserEmail)) {
            throw new RuntimeException("Administradores não podem alterar seu próprio status.");
        }
        
        user.setActive(!user.isActive());

        return userRepository.save(user);
    }
    
    public void deleteUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedUserEmail = authentication.getName();

        if (user.getEmail().equals(loggedUserEmail)) {
            throw new RuntimeException("O administrador não pode excluir a própria conta.");
        }

        if (user.getRole() == Role.ADMIN) {

            long adminCount = userRepository.countByRole(Role.ADMIN);

            if (adminCount <= 1) {
                throw new RuntimeException("Não é possível excluir o último ADMINISTRADOR do sistema.");
            }
        }

        userRepository.delete(user);
    }
    /**
     * buscar usuário pelo email (que vem do JWT)
    **/
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    public User updateUserProfile(String email, UpdateUserProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        if (request.getFavoriteGenres() != null) {

        	Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getFavoriteGenres()));

            user.setFavoriteGenres(genres);
        }

        return userRepository.save(user);
    }
    
    public UserResponse mapToResponse(User user) {

    	Set<GenreResponse> genres = null;

    	if (user.getFavoriteGenres() != null) {
            genres = user.getFavoriteGenres()
                    .stream()
                    .map(genre -> GenreResponse.builder()
                            .id(genre.getId())
                            .name(genre.getName())
                            .build())
                    .collect(Collectors.toSet());
        }

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .favoriteGenres(genres)
                .build();
    }
}