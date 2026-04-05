package com.wagnerdf.arcademanager.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO responsável por representar o resumo da biblioteca do usuário.
 */
@Data
@Builder
public class UserDashboardResponse {

    private long totalGames;
    private long completed;
    private long playing;
    private long backlog;
    private long wishlist;
}