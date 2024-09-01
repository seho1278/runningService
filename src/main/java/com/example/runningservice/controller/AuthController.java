package com.example.runningservice.controller;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.LoginRequestDto;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto,
        HttpServletResponse response)
        throws Exception {
        JwtResponse jwtResponse = authService.authenticate(loginRequestDto);

        //responseHeader에 쿠키 저장
        setResponseHeader(response, jwtResponse);

        return ResponseEntity.ok(jwtResponse.getAccessJwt());
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<String> refreshToken(
        HttpServletRequest request, Principal principal,
        HttpServletResponse response) {

        String refreshToken = extractTokenFromCookie(request);

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.NO_VALID_REFRESH_TOKEN);
        }

        JwtResponse jwtResponse = authService.refreshToken(refreshToken, principal);

        setResponseHeader(response, jwtResponse);

        return ResponseEntity.ok(jwtResponse.getAccessJwt());
    }

    private void setResponseHeader(HttpServletResponse response, JwtResponse jwtResponse) {
        response.setHeader("Set-Cookie",
            createRefreshTokenCookie(jwtResponse.getRefreshJwt()).toString());
    }


    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .maxAge(7 * 24 * 60 * 60) // 7일
            .path("/")
            .secure(false)
            .sameSite("None")
            .httpOnly(true)
            .build();
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키에서 토큰을 찾지 못한 경우
    }
}
