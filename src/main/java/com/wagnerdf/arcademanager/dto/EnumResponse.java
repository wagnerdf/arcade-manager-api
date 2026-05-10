package com.wagnerdf.arcademanager.dto;

public class EnumResponse {

    private String code;
    private String label;

    public EnumResponse(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
