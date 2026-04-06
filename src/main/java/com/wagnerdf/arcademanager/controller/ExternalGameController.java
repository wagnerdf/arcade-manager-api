package com.wagnerdf.arcademanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.RawgGameDTO;
import com.wagnerdf.arcademanager.entity.Platform;
import com.wagnerdf.arcademanager.repository.PlatformRepository;
import com.wagnerdf.arcademanager.service.RawgService;

import lombok.RequiredArgsConstructor;

/**
 * Controller responsável pela integração com a API externa de jogos (RAWG).
 *
 * Este controller expõe endpoints para busca e consulta de jogos externos,
 * permitindo que o sistema utilize dados de uma API pública sem persistir
 * diretamente no banco (a menos que necessário).
 *
 * Os dados retornados são encapsulados em DTOs para evitar acoplamento
 * direto com a estrutura da API externa.
 */
@RestController
@RequestMapping("/api/external/games")
@RequiredArgsConstructor
public class ExternalGameController {

    private final RawgService rawgService;
    private final PlatformRepository platformRepository;

    /**
     * Realiza a busca de jogos na API externa com base no nome e plataforma opcional.
     *
     * Este endpoint permite buscar jogos utilizando o nome informado.
     * Opcionalmente, pode-se filtrar por plataforma, onde o ID da plataforma
     * interna é convertido para um termo compatível com a API externa.
     *
     * @param name Nome do jogo a ser pesquisado
     * @param platformId (Opcional) ID da plataforma cadastrada no sistema
     * @return Lista de jogos encontrados na API externa
     *
     * @throws RuntimeException Caso a plataforma informada não seja encontrada
     */
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

    /**
     * Mapeia o nome da plataforma interna para um termo reconhecido pela API RAWG.
     *
     * Este método faz uma normalização simples baseada em palavras-chave
     * para garantir compatibilidade com os parâmetros aceitos pela API externa.
     *
     * @param platformName Nome da plataforma cadastrada no sistema
     * @return Termo correspondente para uso na API externa
     */
    private String mapPlatform(String platformName) {

        String name = platformName.toLowerCase();

        if (name.contains("playstation")) return "playstation";
        if (name.contains("xbox")) return "xbox";
        if (name.contains("pc")) return "pc";
        if (name.contains("nintendo")) return "nintendo";

        return "";
    }
    
    /**
     * Busca os detalhes de um jogo específico na API externa pelo seu ID.
     *
     * O ID utilizado é o identificador da API externa (RAWG),
     * permitindo recuperar informações detalhadas de um jogo específico.
     *
     * @param externalId ID do jogo na API externa
     * @return Dados detalhados do jogo
     */
    @GetMapping("/{externalId}")
    public RawgGameDTO getGameById(@PathVariable Long externalId) {
        return rawgService.getGameById(externalId);
    }
}
