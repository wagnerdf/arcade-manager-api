package com.wagnerdf.arcademanager.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.ChangePasswordRequest;
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

    /**
     * Registra um novo usuário no sistema.
     * 
     * Regras aplicadas:
     * - Email é convertido para lowercase
     * - Email deve ser único
     * - Senha é criptografada antes de salvar
     * - Novo usuário recebe papel USER por padrão
     * - Conta é criada como ativa
     */
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
     * Retorna a lista de todos os usuários cadastrados no sistema.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Promove um usuário para o papel ADMIN.
     * 
     * Regras de segurança:
     * - Um ADMIN não pode promover a si mesmo
     * - O usuário deve existir no sistema
     * - Usuário não pode já possuir papel ADMIN
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
     * Reverte um ADMIN para o papel USER.
     * 
     * Regras de segurança:
     * - Um ADMIN não pode remover seu próprio papel
     * - Usuário deve existir no sistema
     * - Usuário deve possuir papel ADMIN
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
    
    /**
     * Alterna o status ativo/inativo de um usuário.
     * 
     * Regras aplicadas:
     * - Um administrador não pode alterar o próprio status
     * - O status é invertido (ativo → inativo / inativo → ativo)
     */
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
    
    /**
     * Remove permanentemente um usuário do sistema.
     * 
     * Regras de segurança:
     * - Administrador não pode excluir a própria conta
     * - O último ADMIN do sistema não pode ser removido
     */
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
     * Busca um usuário pelo email.
     * 
     * Utilizado principalmente para recuperar o usuário
     * autenticado através do email presente no token JWT.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    /**
     * Atualiza os dados de perfil do usuário autenticado.
     * 
     * Campos que podem ser alterados:
     * - Nome completo
     * - Telefone
     * - Endereço
     * - Gêneros favoritos
     * 
     * Apenas os campos informados na requisição são atualizados.
     */
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
    
    /**
     * Converte a entidade User para o DTO UserResponse.
     * 
     * Remove campos sensíveis e formata os dados para
     * retorno seguro na API.
     * 
     * Também converte os gêneros favoritos para
     * GenreResponse (id e name).
     */
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
    
    /**
     * Altera a senha do usuário autenticado.
     *
     * Regras:
     * - senha atual deve estar correta
     * - nova senha e confirmação devem ser iguais
     * - nova senha é criptografada antes de salvar
     */
    public void changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        // valida senha atual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual incorreta", HttpStatus.BAD_REQUEST);
        }

        // valida confirmação da nova senha
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Nova senha e confirmação não conferem", HttpStatus.BAD_REQUEST);
        }

        // criptografa nova senha
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
    
    /**
     * Retorna usuários paginados.
     * Utiliza Pageable para limitar quantidade de registros retornados.
     */
    public Page<UserResponse> getUsers(Pageable pageable) {

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(this::mapToResponse);
    }
    
    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
    }
}