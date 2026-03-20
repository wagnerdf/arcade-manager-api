package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.enums.MediaType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGameResponse {

    private String id;

    private String gameTitle;

    private Set<String> platforms;

    private Set<String> genres;
    
    private Long externalGameId;

    private MediaType mediaType;

    private GameStatus status;
}
