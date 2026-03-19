package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.RawgGameDTO;
import com.wagnerdf.arcademanager.entity.Platform;
import com.wagnerdf.arcademanager.repository.PlatformRepository;
import com.wagnerdf.arcademanager.service.RawgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/external/games")
@RequiredArgsConstructor
public class ExternalGameController {

    private final RawgService rawgService;
    private final PlatformRepository platformRepository;

    @GetMapping
    public List<RawgGameDTO> searchGames(
            @RequestParam String name,
            @RequestParam(required = false) String platformId) {

        String platformTerm = "";

        if (platformId != null) {
            Platform platform = platformRepository.findById(platformId)
                    .orElseThrow(() -> new RuntimeException("Platform not found"));

            platformTerm = mapPlatform(platform.getName());
        }

        return rawgService.searchGames(name, platformTerm);
    }

    private String mapPlatform(String platformName) {

        String name = platformName.toLowerCase();

        if (name.contains("playstation")) return "playstation";
        if (name.contains("xbox")) return "xbox";
        if (name.contains("pc")) return "pc";
        if (name.contains("nintendo")) return "nintendo";

        return "";
    }
}
