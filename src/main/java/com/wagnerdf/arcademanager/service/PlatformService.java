package com.wagnerdf.arcademanager.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.wagnerdf.arcademanager.dto.CreatePlatformRequest;
import com.wagnerdf.arcademanager.dto.UpdatePlatformRequest;
import com.wagnerdf.arcademanager.entity.Platform;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.PlatformRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformRepository platformRepository;

    /**
     * Cria uma nova plataforma (console) no sistema.
     * 
     * Regras aplicadas:
     * - O nome da plataforma deve ser único
     * - A verificação ignora diferença entre maiúsculas e minúsculas
     */
    public Platform createPlatform(CreatePlatformRequest request) {

        if (platformRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Plataforma já cadastrada", HttpStatus.CONFLICT);
        }

        Platform platform = Platform.builder()
                .name(request.getName())
                .manufacturer(request.getManufacturer())
                .releaseYear(request.getReleaseYear())
                .unitsSold(request.getUnitsSold())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        return platformRepository.save(platform);
    }
    
    /**
     * Retorna todas as plataformas cadastradas no sistema.
     * 
     * Utilizado principalmente para:
     * - seleção de plataforma ao cadastrar jogos
     * - exibição da lista de consoles disponíveis
     */
    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }
    
    /**
     * Atualiza os dados de uma plataforma existente.
     * 
     * Regras aplicadas:
     * - A plataforma deve existir
     * - O novo nome não pode duplicar outra plataforma
     */
    public Platform updatePlatform(String id, UpdatePlatformRequest request) {

        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plataforma não encontrada", HttpStatus.NOT_FOUND));

        if (platformRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Já existe uma plataforma com esse nome", HttpStatus.CONFLICT);
        }

        platform.setName(request.getName());
        platform.setManufacturer(request.getManufacturer());
        platform.setReleaseYear(request.getReleaseYear());
        platform.setUnitsSold(request.getUnitsSold());
        platform.setDescription(request.getDescription());
        platform.setImageUrl(request.getImageUrl());

        return platformRepository.save(platform);
    }
    
    /**
     * Remove uma plataforma do sistema.
     * 
     * Regras aplicadas:
     * - A plataforma deve existir no sistema
     */
    public void deletePlatform(String id) {

        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plataforma não encontrada", HttpStatus.NOT_FOUND));

        platformRepository.delete(platform);
    }
}