package com.wagnerdf.arcademanager.dto;

import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.enums.MediaType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddUserGameRequest {

    @NotNull
    private Long externalId;

    @NotNull
    private MediaType mediaType;

    private GameStatus status;
}
