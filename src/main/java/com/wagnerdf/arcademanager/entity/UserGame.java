package com.wagnerdf.arcademanager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.wagnerdf.arcademanager.enums.GameStatus;
import com.wagnerdf.arcademanager.enums.MediaType;

import lombok.*;

@Document(collection = "user_games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGame {

    @Id
    private String id;

    private User user;

    private Game game;

    private Platform platform;

    private MediaType mediaType;

    private GameStatus status;

}
