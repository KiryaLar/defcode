package com.larkin.defcode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationResponse {

    private String role;
    private String accessToken;
    private String refreshToken;
}
