package com.wagnerdf.arcademanager.dto;

import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.enums.MediaType;

import lombok.Data;

@Data
public class AddUserGameRequest {

    private String gameId;

    private String platformId;

    private MediaType mediaType;

    private GameStatus status;

}
