package com.wagnerdf.arcademanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawgService {

    private final RestTemplate restTemplate;

    @Value("${rawg.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://api.rawg.io/api/games";

    public String searchGames(String name, String platformTerm) {

        String url = BASE_URL +
                "?key=" + apiKey +
                "&search=" + name +
                "&search_precise=true";

        if (platformTerm != null && !platformTerm.isEmpty()) {
            url += "&search=" + name + " " + platformTerm;
        }

        return restTemplate.getForObject(url, String.class);
    }
}