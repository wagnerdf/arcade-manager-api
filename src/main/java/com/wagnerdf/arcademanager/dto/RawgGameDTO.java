package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RawgGameDTO {

    private Long externalId;

    private String name;

    private String released;

    private String backgroundImage;

    private Set<String> platforms;

    private Set<String> genres;
}