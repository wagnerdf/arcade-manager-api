package com.wagnerdf.arcademanager.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {

    private String email;
    private String role;

}
