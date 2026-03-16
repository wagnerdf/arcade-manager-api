package com.wagnerdf.arcademanager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "platforms")
public class Platform {

    @Id
    private String id;

    private String name;

    private String manufacturer;

    private Integer releaseYear;

    private Long unitsSold;

    private String description;

    private String imageUrl;

}