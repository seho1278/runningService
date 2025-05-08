package com.example.runningservice.service;
//
//import com.example.runningservice.dto.googleToken.GoogleAccessTokenResponseDto;
//import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
//import com.example.runningservice.exception.CustomException;
//import com.example.runningservice.exception.ErrorCode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
public class OAuth2ServiceTest {
//
//    @InjectMocks
//    private OAuth2Service oAuth2Service; // 테스트할 서비스 클래스
//
//    @Mock
//    private RestTemplate restTemplate; // RestTemplate 모킹
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this); // Mockito 초기화
//    }
//
//    @Test
//    public void testGetGoogleAccountProfile_Success() {
//        // 테스트 데이터 설정
//        String code = "dummyCode";
//        String accessToken = "dummyAccessToken";
//        GoogleAccessTokenResponseDto accessTokenResponse = GoogleAccessTokenResponseDto.builder()
//            .access_token(accessToken)
//            .build();
//        GoogleAccountProfileResponseDto profileResponse = GoogleAccountProfileResponseDto.builder()
//            .email("test@example.com")
//            .build();
//
//        // RestTemplate의 응답을 모킹
//        when(restTemplate.exchange(
//            eq("url.access-token"),
//            eq(HttpMethod.POST),
//            any(HttpEntity.class),
//            eq(GoogleAccessTokenResponseDto.class)))
//            .thenReturn(ResponseEntity.ok(accessTokenResponse));
//        when(restTemplate.exchange(
//            eq("url.profile"),
//            eq(HttpMethod.GET),
//            any(HttpEntity.class),
//            eq(GoogleAccountProfileResponseDto.class)))
//            .thenReturn(ResponseEntity.ok(profileResponse));
//
//        // 테스트할 메서드 호출
//        GoogleAccountProfileResponseDto result = oAuth2Service.getGoogleAccountProfile(code);
//
//        // 결과 검증
//        assertNotNull(result);
//        assertEquals("test@example.com", result.getEmail());
//
//        // RestTemplate의 호출 검증
//        verify(restTemplate).exchange(
//            eq("url.access-token"),
//            eq(HttpMethod.POST),
//            any(HttpEntity.class),
//            eq(GoogleAccessTokenResponseDto.class));
//        verify(restTemplate).exchange(
//            eq("url.profile"),
//            eq(HttpMethod.GET),
//            any(HttpEntity.class),
//            eq(GoogleAccountProfileResponseDto.class));
//    }
//
//    @Test
//    public void testRequestGoogleAccessToken_Failure() {
//        // 예외 상황을 설정
//        String code = "dummyCode";
//        when(restTemplate.exchange(
//            eq("url.access-token"),
//            eq(HttpMethod.POST),
//            any(HttpEntity.class),
//            eq(GoogleAccessTokenResponseDto.class)))
//            .thenThrow(new RuntimeException("Simulated failure"));
//
//        // 예외 발생 검증
//        CustomException thrown = assertThrows(CustomException.class, () -> oAuth2Service.getGoogleAccountProfile(code));
//
//        // CustomException의 에러 코드 검증
//        assertEquals(ErrorCode.GOOGLE_LOGIN_FAILED, thrown.getErrorCode());
//    }
//
//    @Test
//    public void testRequestGoogleAccountProfile_Success() {
//        // 테스트 데이터 설정
//        String accessToken = "dummyAccessToken";
//        GoogleAccountProfileResponseDto profileResponse = GoogleAccountProfileResponseDto.builder()
//            .email("test@example.com")
//            .build();
//
//        // RestTemplate의 응답을 모킹
//        when(restTemplate.exchange(
//            eq("url.profile"),
//            eq(HttpMethod.GET),
//            any(HttpEntity.class),
//            eq(GoogleAccountProfileResponseDto.class)))
//            .thenReturn(ResponseEntity.ok(profileResponse));
//
//        // 테스트할 메서드 호출
//        GoogleAccountProfileResponseDto result = oAuth2Service.requestGoogleAccountProfile(accessToken);
//
//        // 결과 검증
//        assertNotNull(result);
//        assertEquals("test@example.com", result.getEmail());
//
//        // RestTemplate의 호출 검증
//        verify(restTemplate).exchange(
//            eq("url.profile"),
//            eq(HttpMethod.GET),
//            any(HttpEntity.class),
//            eq(GoogleAccountProfileResponseDto.class));
//    }
}
