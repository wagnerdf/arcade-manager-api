package com.wagnerdf.arcademanager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGameStatsDTO {
    private long total;
    private long playing;
    private long completed;
}
