package com.wagnerdf.arcademanager.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.wagnerdf.arcademanager.dto.RawgGameDTO;
import com.wagnerdf.arcademanager.integration.rawg.dto.RawgResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawgService {

    private final RestTemplate restTemplate;

    @Value("${rawg.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://api.rawg.io/api/games";

    public List<RawgGameDTO> searchGames(String name, String platformTerm) {

        String url = BASE_URL +
                "?key=" + apiKey +
                "&search=" + name +
                "&search_precise=true";

        if (platformTerm != null && !platformTerm.isEmpty()) {
            url += "&search=" + name + " " + platformTerm;
        }

        RawgResponse response = restTemplate.getForObject(url, RawgResponse.class);

        if (response == null || response.getResults() == null) {
            return List.of();
        }

        return response.getResults().stream().map(game -> {

            Set<String> platforms = game.getPlatforms() != null
                    ? game.getPlatforms().stream()
                        .map(p -> p.getPlatform().getName())
                        .collect(Collectors.toSet())
                    : Set.of();

            Set<String> genres = game.getGenres() != null
                    ? game.getGenres().stream()
                        .map(g -> g.getName())
                        .collect(Collectors.toSet())
                    : Set.of();

            return RawgGameDTO.builder()
                    .externalId(game.getId())
                    .name(game.getName())
                    .released(game.getReleased())
                    .backgroundImage(game.getBackground_image())
                    .platforms(platforms)
                    .genres(genres)
                    .build();

        }).collect(Collectors.toList());
    }
}