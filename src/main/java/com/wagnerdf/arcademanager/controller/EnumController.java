package com.wagnerdf.arcademanager.controller;

import com.wagnerdf.arcademanager.dto.EnumResponse;
import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.enums.MediaType;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

	/**
	 * Lista todos os status possíveis de jogos.
	 *
	 * Utilizado pelo frontend para popular seletores de status
	 * ao adicionar ou editar jogos na biblioteca do usuário.
	 *
	 * @return lista de status com código e descrição amigável
	 */
    @GetMapping("/game-status")
    public List<EnumResponse> getGameStatus() {
        return Arrays.stream(GameStatus.values())
                .map(status -> new EnumResponse(
                        status.name(),
                        status.getDescription()
                ))
                .toList();
    }
    
    /**
     * Lista todos os tipos de mídia disponíveis para jogos.
     *
     * Utilizado pelo frontend para definir como o jogo é armazenado
     * (ex: digital, CD, cartucho).
     *
     * @return lista de tipos de mídia com código e descrição amigável
     */
    @GetMapping("/media-type")
    public List<EnumResponse> getMediaTypes() {
        return Arrays.stream(MediaType.values())
                .map(type -> new EnumResponse(
                        type.name(),
                        type.getDescription()
                ))
                .toList();
    }
}
