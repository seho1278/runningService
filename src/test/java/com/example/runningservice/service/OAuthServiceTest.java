package com.example.runningservice.service;

import com.example.runningservice.dto.GoogleAccessTokenRequestDto;
import com.example.runningservice.dto.GoogleAccessTokenResponseDto;
import com.example.runningservice.dto.GoogleAccountProfileResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.LoginException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class OAuth2ServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OAuth2Service oAuth2Service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetGoogleAccountProfile() throws LoginException {
        // GoogleAccessTokenResponseDto
        GoogleAccessTokenResponseDto accessTokenResponseDto = new GoogleAccessTokenResponseDto();
        accessTokenResponseDto.setAccessToken("mockAccessToken");

        // GoogleAccountProfileResponseDto
        GoogleAccountProfileResponseDto profileResponseDto = new GoogleAccountProfileResponseDto();
        profileResponseDto.setEmail("test@example.com");
        profileResponseDto.setName("Test User");

        // RestTemplate 세팅해서 Mock Test
        when(restTemplate.exchange(
            "http://mockAccessTokenUrl",
            HttpMethod.POST,
            new HttpEntity<>(new GoogleAccessTokenRequestDto("clientId", "clientSecret", "code", "redirectUri"), new HttpHeaders()),
            GoogleAccessTokenResponseDto.class))
            .thenReturn(ResponseEntity.ok(accessTokenResponseDto));

        when(restTemplate.exchange(
            "http://mockProfileUrl",
            HttpMethod.GET,
            new HttpEntity<>(new HttpHeaders().set("Authorization", "Bearer mockAccessToken")),
            GoogleAccountProfileResponseDto.class))
            .thenReturn(ResponseEntity.ok(profileResponseDto));

        // OAuth2Service.getGoogleAccountProfile
        GoogleAccountProfileResponseDto result = oAuth2Service.getGoogleAccountProfile("mockCode");

        // 결과확인
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
    }
}
