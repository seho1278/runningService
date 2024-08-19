package com.example.runningservice.dto.googleToken;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleAccessTokenResponseDto {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
