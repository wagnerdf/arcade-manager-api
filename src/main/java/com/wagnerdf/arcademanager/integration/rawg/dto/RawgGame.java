package com.wagnerdf.arcademanager.integration.rawg.dto;

import java.util.List;

import lombok.Data;

@Data
public class RawgGame {

    private Long id;
    private String name;
    private String released;
    private String background_image;

    private List<PlatformWrapper> platforms;
    private List<Genre> genres;
}
