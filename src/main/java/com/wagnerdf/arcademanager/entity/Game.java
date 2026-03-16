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

    private String title;

    private Platform platform;

    private Set<Genre> genres;

    private Integer releaseYear;

    private String developer;

    private String description;

}