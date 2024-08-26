package com.example.runningservice.dto.googleToken;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleAccountProfileResponseDto {
    private String id;
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String locale;
}
