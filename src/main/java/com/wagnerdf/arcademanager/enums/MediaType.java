package com.wagnerdf.arcademanager.enums;

public enum MediaType {

    CARTRIDGE("Cartucho"),
    CD("CD"),
    DIGITAL("Digital");

    private final String description;

    MediaType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
