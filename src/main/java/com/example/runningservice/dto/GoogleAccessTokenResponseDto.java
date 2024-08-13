package com.example.runningservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleAccessTokenResponseDto {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private String scope;
    private String expiresIn;
    private String refreshExpiresIn;
    private String uid;
    private String email;
    private String displayName;
    private String avatarUrl;
}
