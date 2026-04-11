package com.wagnerdf.arcademanager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.ChangePasswordRequest;
import com.wagnerdf.arcademanager.dto.RegisterUserRequest;
import com.wagnerdf.arcademanager.dto.UpdateAddressRequest;
import com.wagnerdf.arcademanager.dto.UpdateUserProfileRequest;
import com.wagnerdf.arcademanager.dto.UserDashboardResponse;
import com.wagnerdf.arcademanager.dto.UserResponse;
import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.UserRepository;
import com.wagnerdf.arcademanager.service.UserGameService;
import com.wagnerdf.arcademanager.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    private final UserRepository userRepository;
    
    private final UserGameService userGameService;

    /**
     * Registrar um novo usuário no sistema
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterUserRequest request) {

        User createdUser = userService.registerUser(request);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Listar usuários com paginação
     * Acesso restrito a administradores
     * GET /api/users?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Promover um usuário para o papel de ADMIN
     * Ação permitida apenas para administradores
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
     * Reverter um usuário ADMIN para o papel USER
     * Ação permitida apenas para administradores
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
     * Habilitar ou desabilitar um usuário
     * Alterna o status ativo/inativo da conta
     * Acesso restrito a administradores
     * PUT /api/users/{id}/status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUserStatus(@PathVariable String id) {

        User updatedUser = userService.toggleUserStatus(id);

        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Deletar permanentemente um usuário do sistema
     * Acesso restrito a administradores
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
    
    /**
     * Retornar o perfil do usuário autenticado
     * Utiliza o email presente no token JWT
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {

        User user = userService.findByEmail(authentication.getName());

        return ResponseEntity.ok(userService.mapToResponse(user));
    }
    
    /**
     * Atualizar os dados de perfil do usuário autenticado
     * Permite editar: nome, telefone, endereço e gêneros favoritos
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
    		@Valid @RequestBody UpdateUserProfileRequest request,
            Authentication authentication) {

        User updatedUser = userService.updateUserProfile(authentication.getName(), request);

        return ResponseEntity.ok(userService.mapToResponse(updatedUser));
    }
    
    /**
     * Alterar senha do usuário autenticado
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
    		@Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        userService.changePassword(authentication.getName(), request);

        return ResponseEntity.noContent().build();
    }
    
    /**
     * Atualizar o endereço do usuário autenticado
     * 
     * Permite alterar todos os dados de endereço do usuário.
     * O endereço é sobrescrito completamente.
     * 
     * PUT /api/users/me/address
     */
    @PutMapping("/me/address")
    public ResponseEntity<UserResponse> updateAddress(
    		@Valid @RequestBody UpdateAddressRequest request,
            Authentication authentication) {

        User updatedUser = userService.updateAddress(authentication.getName(), request);

        return ResponseEntity.ok(userService.mapToResponse(updatedUser));
    }
    
    /**
     * Endpoint responsável por retornar o resumo da biblioteca do usuário autenticado.
     *
     * O dashboard apresenta a quantidade total de jogos e a distribuição por status,
     * como jogos finalizados, em andamento, backlog e lista de desejos.
     *
     * Fluxo:
     * - Recupera o usuário autenticado a partir do contexto de segurança (Spring Security)
     * - Busca o usuário na base de dados
     * - Consulta os jogos do usuário e calcula as estatísticas
     *
     * @param authentication objeto de autenticação do Spring Security
     *
     * @return ResponseEntity contendo o resumo da biblioteca do usuário
     *
     * @throws BusinessException com status NOT_FOUND caso o usuário não seja encontrado
     */
    @GetMapping("/me/dashboard")
    public ResponseEntity<UserDashboardResponse> getDashboard(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new BusinessException(
                "User not found",
                HttpStatus.NOT_FOUND
            ));

        UserDashboardResponse response = userGameService.getUserDashboard(user.getId());

        return ResponseEntity.ok(response);
    }
}