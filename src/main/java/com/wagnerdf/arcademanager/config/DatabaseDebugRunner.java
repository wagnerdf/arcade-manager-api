package com.wagnerdf.arcademanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wagnerdf.arcademanager.entity.Role;
import com.wagnerdf.arcademanager.repository.RoleRepository;
import com.wagnerdf.arcademanager.repository.UserRepository;

@Configuration
public class DatabaseDebugRunner {

    @Bean
    CommandLineRunner debugDatabase(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {

            System.out.println("=================================");
            System.out.println("VERIFICANDO ROLES NO BANCO");
            System.out.println("=================================");

            if (roleRepository.count() == 0) {

                System.out.println("Nenhuma role encontrada. Criando roles padrão...");

                Role admin = Role.builder()
                        .name("ADMIN")
                        .build();

                Role user = Role.builder()
                        .name("USER")
                        .build();

                roleRepository.save(admin);
                roleRepository.save(user);

                System.out.println("Roles ADMIN e USER criadas.");
            }
            System.out.println("Mongo URI -> mongodb://localhost:27017/arcade-manager");

            System.out.println("=================================");
            System.out.println("LISTANDO ROLES");
            System.out.println("=================================");

            roleRepository.findAll().forEach(role -> {
                System.out.println("ROLE -> " + role);
            });

            System.out.println("=================================");
            System.out.println("TOTAL DE USERS: " + userRepository.count());
            System.out.println("=================================");

        };
    }
}