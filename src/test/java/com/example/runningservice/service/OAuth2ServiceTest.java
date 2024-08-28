package com.example.runningservice.service;

import com.example.runningservice.dto.googleToken.GoogleAccessTokenRequestDto;
import com.example.runningservice.dto.googleToken.GoogleAccessTokenResponseDto;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
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
    public void testGetGoogleAccountProfile() throws Exception {
        // GoogleAccessTokenResponseDto
        GoogleAccessTokenResponseDto accessTokenResponseDto = GoogleAccessTokenResponseDto.builder()
                .access_token("mockAccessToken")
                .build();


        // GoogleAccountProfileResponseDto
        GoogleAccountProfileResponseDto profileResponseDto = GoogleAccountProfileResponseDto.builder()
            .email("test@example.com")
            .name("Test User")
            .build();

        // RestTemplate 세팅해서 Mock Test
        when(restTemplate.exchange(
            "http://mockAccessTokenUrl",
            HttpMethod.POST,
            new HttpEntity<>(GoogleAccessTokenRequestDto.builder()
                .client_id("clientId")
                .client_secret("clientSecret")
                .code("code")
                .redirect_uri("redirectUri")
                .build(), new HttpHeaders()),
            GoogleAccessTokenResponseDto.class))
            .thenReturn(ResponseEntity.ok(accessTokenResponseDto));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mockAccessToken");
        // HttpEntity 설정
        HttpEntity<GoogleAccountProfileResponseDto> entity = new HttpEntity<>(null, headers);

        when(restTemplate.exchange(
            "http://mockProfileUrl",
            HttpMethod.GET,
            entity,
            GoogleAccountProfileResponseDto.class))
            .thenReturn(ResponseEntity.ok(profileResponseDto));


        // OAuth2Service.getGoogleAccountProfile
        GoogleAccountProfileResponseDto result = oAuth2Service.getGoogleAccountProfile("mockCode");

        // 결과확인
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
    }
}
