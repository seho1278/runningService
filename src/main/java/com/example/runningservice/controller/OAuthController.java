package com.example.runningservice.controller;

import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.service.OAuth2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {
    private final OAuth2Service oAuth2Service;
    public OAuthController(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @GetMapping("login/oauth")
    public ResponseEntity<GoogleAccountProfileResponseDto> googleAccountProfileRes(@RequestParam String code) {
        return ResponseEntity.ok(oAuth2Service.getGoogleAccountProfile(code));
    }

}
