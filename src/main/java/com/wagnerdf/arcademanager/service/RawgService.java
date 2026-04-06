package com.wagnerdf.arcademanager.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.wagnerdf.arcademanager.dto.RawgGameDTO;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.integration.rawg.dto.RawgGame;
import com.wagnerdf.arcademanager.integration.rawg.dto.RawgResponse;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela integração com a API externa RAWG.
 *
 * Este serviço realiza chamadas HTTP para a API pública RAWG,
 * tratando as respostas e convertendo os dados para DTOs internos
 * utilizados pela aplicação.
 *
 * Também centraliza o tratamento de erros relacionados à API externa,
 * garantindo que o restante do sistema não dependa diretamente da RAWG.
 */
@Service
@RequiredArgsConstructor
public class RawgService {

    private final RestTemplate restTemplate;

    @Value("${rawg.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://api.rawg.io/api/games";

    /**
     * Realiza a busca de jogos na API RAWG com base no nome e plataforma.
     *
     * Este método monta dinamicamente a URL de consulta, combinando o nome
     * do jogo com o termo da plataforma (quando informado), para melhorar
     * a precisão dos resultados retornados.
     *
     * Os dados retornados pela API são transformados em {@link RawgGameDTO},
     * incluindo informações como nome, data de lançamento, imagem,
     * plataformas e gêneros.
     *
     * @param name Nome do jogo a ser pesquisado
     * @param platformTerm Termo da plataforma (ex: "pc", "playstation"), pode ser vazio
     * @return Lista de jogos encontrados convertidos em DTOs
     */
    public List<RawgGameDTO> searchGames(String name, String platformTerm) {

    	String searchTerm = name;

    	if (platformTerm != null && !platformTerm.isEmpty()) {
    	    searchTerm += " " + platformTerm;
    	}

    	String url = BASE_URL +
    	        "?key=" + apiKey +
    	        "&search=" + searchTerm +
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
    
    /**
     * Busca os detalhes de um jogo específico na API RAWG.
     *
     * Este método consulta a API externa utilizando o ID do jogo e retorna
     * informações detalhadas convertidas em {@link RawgGameDTO}.
     *
     * Inclui tratamento de erros para cenários como:
     * 
     *   Jogo não encontrado (404)
     *   Erros genéricos da API
     *
     * @param externalId ID do jogo na API RAWG
     * @return Dados detalhados do jogo
     *
     * @throws BusinessException Caso o jogo não seja encontrado na API externa
     * @throws RuntimeException Para outros erros de comunicação com a API
     */
    public RawgGameDTO getGameById(Long externalId) {

        try {

            String url = BASE_URL +
                    "/" + externalId +
                    "?key=" + apiKey;

            RawgGame response =
                    restTemplate.getForObject(url, RawgGame.class);

            if (response == null) {
                throw new RuntimeException("Game not found on RAWG");
            }

            Set<String> platforms = response.getPlatforms() != null
                    ? response.getPlatforms().stream()
                        .map(p -> p.getPlatform().getName())
                        .collect(Collectors.toSet())
                    : Set.of();

            Set<String> genres = response.getGenres() != null
                    ? response.getGenres().stream()
                        .map(g -> g.getName())
                        .collect(Collectors.toSet())
                    : Set.of();

            return RawgGameDTO.builder()
                    .externalId(response.getId())
                    .name(response.getName())
                    .released(response.getReleased())
                    .backgroundImage(response.getBackground_image())
                    .platforms(platforms)
                    .genres(genres)
                    .build();

        } catch (HttpClientErrorException.NotFound e) {

        	throw new BusinessException("Game não encontrado na RAWG", HttpStatus.NOT_FOUND);

        } catch (HttpClientErrorException e) {

            throw new RuntimeException("Erro ao consultar RAWG: " + e.getStatusCode());
        }
    }
}