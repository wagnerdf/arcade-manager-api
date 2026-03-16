package com.wagnerdf.arcademanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.CreatePlatformRequest;
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
}
