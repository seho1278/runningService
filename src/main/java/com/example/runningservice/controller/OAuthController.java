package com.example.runningservice.controller;

import com.example.runningservice.dto.auth.AdditionalInfoRequestDto;
import com.example.runningservice.dto.auth.JwtResponse;
import com.example.runningservice.dto.auth.Oauth2DataDto;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.service.OAuth2Service;
import com.example.runningservice.service.Oauth2ProcessService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuth2Service oAuth2Service;
    private final Oauth2ProcessService oauth2ProcessService;

    @GetMapping("login/oauth")
    public ResponseEntity<String> googleAccountProfileRes(
        @RequestParam String code, HttpServletResponse response) throws IOException {

        GoogleAccountProfileResponseDto googleAccountProfile = oAuth2Service.getGoogleAccountProfile(
            code);

        JwtResponse jwtResponse = oauth2ProcessService.processOauth2Info(
            googleAccountProfile, response);

        if (response.isCommitted()) {
            // 리디렉트가 발생했으면 메서드를 종료함
            return null;
        }

        setResponseHeader(response, jwtResponse);

        return ResponseEntity.ok(jwtResponse.getAccessJwt());
    }

    //필수정보 확인
    @GetMapping("/user/signup/saved-info")
    public ResponseEntity<Oauth2DataDto> showAdditionalInfoForm(
        @RequestParam String email) {
        return ResponseEntity.ok(oauth2ProcessService.getOauthData(email));
    }

    //채워지지 않은 필수정보 입력
    @PostMapping("/user/signup/additional-info")
    public ResponseEntity<String> completeOauth(
        @ModelAttribute @Valid AdditionalInfoRequestDto form, HttpServletResponse response) {

        JwtResponse jwtResponse = oauth2ProcessService.completeSignup(form);

        setResponseHeader(response, jwtResponse);

        return ResponseEntity.ok(jwtResponse.getAccessJwt());
    }

    private void setResponseHeader(HttpServletResponse response, JwtResponse jwtResponse) {
        response.setHeader("Set-Cookie",
            createRefreshTokenCookie(jwtResponse.getRefreshJwt()).toString());
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .maxAge(7 * 24 * 60 * 60) // 7알
            .path("/")
            .secure(false)
            .sameSite("None")
            .httpOnly(true)
            .build();
    }
}
