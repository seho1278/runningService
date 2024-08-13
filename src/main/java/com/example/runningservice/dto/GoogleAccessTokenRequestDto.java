package com.example.runningservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleAccessTokenRequestDto {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
}
