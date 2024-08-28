package com.example.runningservice.controller;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.LoginRequestDto;
import com.example.runningservice.service.AuthService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequestDto loginRequestDto)
        throws Exception {
        JwtResponse jwtResponse = authService.authenticate(loginRequestDto);

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
        @RequestHeader(name = "Authorization") String refreshToken, Principal principal) {

        refreshToken = refreshToken.replace("Bearer ", "");
        return ResponseEntity.ok(authService.refreshToken(refreshToken, principal));
    }

}
