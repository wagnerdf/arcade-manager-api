package com.wagnerdf.arcademanager.integration.rawg.dto;

import java.util.List;

import lombok.Data;

@Data
public class RawgResponse {
    private List<RawgGame> results;
}