package com.wagnerdf.arcademanager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    private String id;

    private String name;
}