package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import lombok.Data;

@Data
public class CreateGameRequest {

    private String title;

    private String platformId;

    private Set<String> genreIds;

    private Integer releaseYear;

    private String developer;

    private String description;

}
