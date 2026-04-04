package com.wagnerdf.arcademanager.dto;

import lombok.Data;

@Data
public class UpdateAddressRequest {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
