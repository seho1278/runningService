package com.example.runningservice.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Oauth2DataDto {
    String email;
    String name;
    String image;
}
