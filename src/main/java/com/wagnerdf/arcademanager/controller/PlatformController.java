package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.CreatePlatformRequest;
import com.wagnerdf.arcademanager.dto.UpdatePlatformRequest;
import com.wagnerdf.arcademanager.entity.Platform;
import com.wagnerdf.arcademanager.service.PlatformService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    /**
     * Criar uma nova plataforma no sistema.
     * Apenas ADMIN pode realizar esta operação.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Platform> createPlatform(@RequestBody CreatePlatformRequest request) {

        Platform platform = platformService.createPlatform(request);

        return new ResponseEntity<>(platform, HttpStatus.CREATED);
    }
    
    /**
     * Listar todas as plataformas cadastradas.
     */
    @GetMapping
    public ResponseEntity<List<Platform>> getPlatforms() {

        List<Platform> platforms = platformService.getAllPlatforms();

        return ResponseEntity.ok(platforms);
    }
    
    /**
     * Atualizar uma plataforma existente.
     * Apenas ADMIN pode realizar esta operação.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Platform> updatePlatform(
            @PathVariable String id,
            @RequestBody UpdatePlatformRequest request) {

        Platform updatedPlatform = platformService.updatePlatform(id, request);

        return ResponseEntity.ok(updatedPlatform);
    }
}
