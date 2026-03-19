package com.wagnerdf.arcademanager.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    private String id;

    private Long externalId;

    private String title;

    // Dados vindos da API
    private Set<String> platforms;

    private Set<String> genres;

    private String released;

    private String developer;

    private String description;

    private String backgroundImage;
}
