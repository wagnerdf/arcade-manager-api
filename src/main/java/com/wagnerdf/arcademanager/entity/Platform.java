package com.wagnerdf.arcademanager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "platforms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Platform {

    @Id
    private String id;

    private String name;

    private String manufacturer;
}