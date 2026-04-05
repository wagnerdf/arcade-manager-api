package com.wagnerdf.arcademanager.dto;

import com.wagnerdf.arcademanager.enums.GameStatus;

import jakarta.validation.constraints.NotNull;

/**
 * DTO responsável por receber a requisição de atualização de status de um jogo do usuário.
 *
 * Contém o novo status que será atribuído ao jogo na biblioteca do usuário.
 */
public class UpdateUserGameStatusRequest {

    @NotNull(message = "Status is required")
    private GameStatus status;

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}