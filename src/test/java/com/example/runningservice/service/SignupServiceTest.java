package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.client.MailgunClient;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.S3FileUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

    @InjectMocks
    private SignupService signupService;

    @Mock
    private MailgunClient mailgunClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AESUtil aesUtil;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3FileUtil s3FileUtil;


    @Test
    void testSignup_success() throws Exception {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .confirmPassword("password")
            .phoneNumber("01011112222")
            .name("name")
            .nickName("nickName")
            .gender(Gender.MALE)
            .birthYear(1900)
            .activityRegion(Region.BUSAN)
            .profileImage(mockFile)
            .build();

        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(aesUtil.encrypt(anyString())).willReturn("encryptedPhoneNumber");
        given(s3FileUtil.getImgUrl(anyString())).willAnswer(invocation -> {
            String fileName = invocation.getArgument(0);
            return "http://test-url.com/" + fileName;
        });
        MemberEntity memberEntity = signupRequestDto.toEntity(passwordEncoder, aesUtil);
        given(memberRepository.save(any())).willReturn(memberEntity);
        given(aesUtil.decrypt(anyString())).willReturn("01011112222");

        // When
        MemberResponseDto responseDto = signupService.signup(signupRequestDto);
        ArgumentCaptor<MemberEntity> captor = ArgumentCaptor.forClass(MemberEntity.class);
        verify(memberRepository).save(captor.capture());
        MemberEntity savedEntity = captor.getValue();

        // Then
        assertNotNull(responseDto);
        verify(s3FileUtil, times(1)).putObject(anyString(), any(MultipartFile.class));
        verify(s3FileUtil, times(1)).getImgUrl(anyString());
        assertEquals("http://test-url.com/user-null", savedEntity.getProfileImageUrl());
        assertEquals("email@email.com", responseDto.getEmail());
        assertEquals("01011112222", responseDto.getPhoneNumber());
        assertEquals("name", responseDto.getName());
        assertEquals("nickName", responseDto.getNickName());
        assertEquals(Gender.MALE, responseDto.getGender());
        assertEquals(1900, responseDto.getBirthYear());
        assertEquals(Region.BUSAN, responseDto.getActivityRegion());
    }

    @Test
    void testSignup_fail_EmailDuplication() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("duplicate@email.com")
            .password("password")
            .confirmPassword("password")
            .phoneNumber("01011112222")
            .name("name")
            .nickName("nickName")
            .gender(Gender.MALE)
            .birthYear(1900)
            .activityRegion(Region.BUSAN)
            .build();

        given(memberRepository.existsByEmail(signupRequestDto.getEmail())).willReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            signupService.signup(signupRequestDto);
        });

        assertEquals(ErrorCode.ALREADY_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    void testSignup_PhoneNumberDuplication() throws Exception {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .confirmPassword("password")
            .phoneNumber("01011112222")
            .name("name")
            .nickName("nickName")
            .gender(Gender.MALE)
            .birthYear(1900)
            .activityRegion(Region.BUSAN)
            .build();

        given(aesUtil.encrypt(signupRequestDto.getPhoneNumber())).willReturn("encryptedPhoneNumber");
        given(memberRepository.existsByPhoneNumberHash("encryptedPhoneNumber")).willReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            signupService.signup(signupRequestDto);
        });

        assertEquals(ErrorCode.ALREADY_EXIST_PHONE, exception.getErrorCode());
    }


    @Test
    void testSignup_NullProfileImage() throws Exception {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("Password123!")
            .confirmPassword("Password123!")
            .phoneNumber("01012345678")
            .name("Kim")
            .nickName("Kim")
            .gender(Gender.MALE)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .profileImage(null)  // 이미지가 null인 경우
            .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(aesUtil.encrypt(anyString())).thenReturn("encryptedPhoneNumber");
        when(memberRepository.save(any(MemberEntity.class))).thenAnswer(invocation -> {
            MemberEntity memberEntity = invocation.getArgument(0);
            memberEntity.setId(1L);
            return memberEntity;
        });

        String defaultImageUrl = "http://test-url.com/user-default";
        when(s3FileUtil.getImgUrl("user-default")).thenReturn(defaultImageUrl);

        // When
        MemberResponseDto responseDto = signupService.signup(signupRequestDto);

        // Then
        verify(s3FileUtil, times(1)).getImgUrl("user-default");
        verify(s3FileUtil, never()).putObject(anyString(), any(MultipartFile.class));
        assertEquals(defaultImageUrl, responseDto.getImageUrl());
    }

    @Test
    void testSendEmail_Success() {
        // Given
        String email = "test@example.com";
        MemberEntity memberEntity = MemberEntity.builder()
            .email(email)
            .name("Kim")
            .build();

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(memberEntity));

        // When
        signupService.sendEmail(email);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);


        // Then
        verify(memberRepository, times(1)).findByEmail(email);

        assertNotNull(memberEntity.getVerificationCode());

        verify(mailgunClient).sendEmail(captor.capture(), captor.capture(), captor.capture(), captor.capture());

        String fromCaptured = captor.getAllValues().get(0);
        String toCaptured = captor.getAllValues().get(1);
        String subjectCaptured = captor.getAllValues().get(2);
        String textCaptured = captor.getAllValues().get(3);

        assertEquals("wadadak@example.com", fromCaptured);
        assertEquals(email, toCaptured);
        assertEquals("Email 인증메일입니다.", subjectCaptured);
        assertTrue(textCaptured.contains("http://localhost:8080/user/signup/email-verify?email=" + email));
        assertTrue(textCaptured.contains(memberEntity.getName()));
        assertTrue(textCaptured.contains(memberEntity.getVerificationCode()));
    }

    @Test
    void testSendEmail_InvalidEmail() {
        // Given
        String email = "notfound@example.com";
        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> signupService.sendEmail(email));
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
        verify(mailgunClient, times(0)).sendEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testVerifyUser_Success() {
        // Given
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.saveVerificationCode("valid-code");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberEntity));

        // When
        signupService.verifyUser("test@example.com", "valid-code");

        // Then
        verify(memberRepository, times(1)).findByEmail(anyString());
        assertEquals(true, memberEntity.isEmailVerified());
    }

    @Test
    void testVerifyUser_InvalidCode() {
        // Given
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.saveVerificationCode("valid-code");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberEntity));

        // When/Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            signupService.verifyUser("test@example.com", "invalid-code");
        });

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, exception.getErrorCode());
    }
}