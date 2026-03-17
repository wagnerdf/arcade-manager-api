package com.wagnerdf.arcademanager.dto;

import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.enums.MediaType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGameResponse {

    private String id;

    private String gameTitle;
    private String platform;

    private MediaType mediaType;
    private GameStatus status;
}
