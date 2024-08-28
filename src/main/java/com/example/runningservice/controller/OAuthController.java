package com.example.runningservice.controller;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.service.OAuth2Service;
import com.example.runningservice.service.Oauth2ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuth2Service oAuth2Service;
    private final Oauth2ProcessService oauth2ProcessService;

    @GetMapping("login/oauth")
    public ResponseEntity<JwtResponse> googleAccountProfileRes(@RequestParam String code) {

        GoogleAccountProfileResponseDto googleAccountProfileResponseDto = oAuth2Service.getGoogleAccountProfile(
            code);

        return ResponseEntity.ok(
            oauth2ProcessService.processOauth2Info(googleAccountProfileResponseDto));
    }

}
