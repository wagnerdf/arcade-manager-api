package com.wagnerdf.arcademanager.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.entity.User;
import com.wagnerdf.arcademanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca usuário pelo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Constrói UserDetails com email, senha e roles
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                // Converte enum Role em SimpleGrantedAuthority com prefixo ROLE_
                user.getRole() != null
                    ? List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                    : List.of()
        );
    }
}