package com.wagnerdf.arcademanager.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdatePlatformRequest {

    private String name;

    private String manufacturer;

    private Integer releaseYear;

    private Long unitsSold;

    private String description;

    private MultipartFile image;

}
