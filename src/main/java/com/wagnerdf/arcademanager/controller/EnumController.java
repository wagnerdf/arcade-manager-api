package com.wagnerdf.arcademanager.controller;

import com.wagnerdf.arcademanager.dto.EnumResponse;
import com.wagnerdf.arcademanager.enums.GameStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/game-status")
    public List<EnumResponse> getGameStatus() {
        return Arrays.stream(GameStatus.values())
                .map(status -> new EnumResponse(
                        status.name(),
                        status.getDescription()
                ))
                .toList();
    }
}
