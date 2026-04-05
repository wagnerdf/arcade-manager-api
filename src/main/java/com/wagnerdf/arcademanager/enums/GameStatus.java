package com.wagnerdf.arcademanager.enums;

/**
 * Representa o status de um jogo na biblioteca do usuário.
 */
public enum GameStatus {

    BACKLOG("Na fila para jogar"),
    PLAYING("Jogando atualmente"),
    COMPLETED("Finalizado / zerado"),
    WISHLIST("Desejo jogar no futuro");
	
	private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    /**
     * Retorna a descrição amigável do status.
     */
    public String getDescription() {
        return description;
    }

}
