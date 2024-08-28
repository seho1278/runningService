package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.LoginRequestDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Role;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.security.CustomUserDetails;
import com.example.runningservice.security.CustomUserDetailsService;
import com.example.runningservice.util.JwtUtil;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private TokenBlackList tokenBlackList;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Principal principal;

    @Test
    void testAuthenticate_success() throws Exception {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password");

        // Mocking roles and authorities
        List<Role> roles = new ArrayList<>(List.of(Role.ROLE_USER));
        Collection<? extends GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());

        // Create a mock CustomUserDetails object
        CustomUserDetails customUserDetails = new CustomUserDetails(1L, "test@example.com",
            "password", roles);

        // Set up the mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(
            customUserDetails); // Return the customUserDetails object
        when(jwtUtil.generateToken(customUserDetails.getUsername(), customUserDetails.getId(),
            new ArrayList<>(customUserDetails.getAuthorities()))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(customUserDetails.getUsername(), customUserDetails.getId(),
            new ArrayList<>(customUserDetails.getAuthorities()))).thenReturn(
            "refresh-token");

        // When
        JwtResponse jwtResponse = authService.authenticate(loginRequestDto);

        // Then
        assertNotNull(jwtResponse);
        assertEquals("access-token", jwtResponse.getAccessJwt());
        assertEquals("refresh-token", jwtResponse.getRefreshJwt());
    }

    @Test
    void testAuthenticate_EmailPasswordMisMatch() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("wrong-password");

        MemberEntity memberEntity = MemberEntity.builder()
            .email("test@example.com")
            .password(passwordEncoder.encode("correct-password"))
            .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.authenticate(loginRequestDto);
        });
        // Then
        assertEquals(ErrorCode.INVALID_LOGIN, exception.getErrorCode());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("nonexistent@example.com");
        loginRequestDto.setPassword("password");

        customUserDetailsService = new CustomUserDetailsService(memberRepository);

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        try {
            when(customUserDetailsService.loadUserByUsername(anyString())).thenCallRealMethod();
        } catch (CustomException ex) {
            assertEquals(ErrorCode.NOT_FOUND_USER, ex.getErrorCode());
        }
    }

    @Test
    void testAuthenticate_EmailNotVerified() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password");

        MemberEntity memberEntity = MemberEntity.builder()
            .email("test@example.com")
            .password(passwordEncoder.encode("password"))
            .emailVerified(false)
            .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberEntity));
        customUserDetailsService = new CustomUserDetailsService(memberRepository);

        try {
            when(customUserDetailsService.loadUserByUsername(anyString())).thenCallRealMethod();
        } catch (CustomException ex) {
            assertEquals(ErrorCode.INVALID_EMAIL, ex.getErrorCode());
        }
    }

    @Test
    void testRefreshToken_success() throws Exception {
        //given
        String refreshToken = "refresh-token";
        String principalEmail = "test@example.com";

        when(tokenBlackList.isListed(refreshToken)).thenReturn(false);
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.extractEmail(refreshToken)).thenReturn("test@example.com");
        when(customUserDetailsService.loadUserByUsername("test@example.com")).thenReturn(
            userDetails);
        List<GrantedAuthority> authorities = new ArrayList<>();
        when(
            jwtUtil.generateToken("test@example.com", userDetails.getId(), authorities)).thenReturn(
            "access-token");
        when(jwtUtil.generateRefreshToken("test@example.com", userDetails.getId(),
            authorities)).thenReturn("refresh-token");
        when(principal.getName()).thenReturn(principalEmail);
        when(jwtUtil.validateToken(principalEmail, refreshToken)).thenCallRealMethod();
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
        //when
        JwtResponse jwtResponse = authService.refreshToken(refreshToken, principal);
        //then
        assertNotNull(jwtResponse);
        assertEquals("refresh-token", jwtResponse.getRefreshJwt());
        assertEquals("access-token", jwtResponse.getAccessJwt());
        verify(tokenBlackList).isListed(refreshToken);
    }

    @Test
    void testRefreshToken_BlacklistedToken() {
        // Given
        String refreshToken = "blacklisted-refresh-token";
        when(tokenBlackList.isListed(refreshToken)).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.refreshToken(refreshToken, principal);
        });

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
        verify(tokenBlackList).isListed(refreshToken);
        verify(jwtUtil, never()).isTokenExpired(anyString());
    }

    @Test
    void testRefreshToken_TokenExpired() {
        // Given
        String refreshToken = "expired-refresh-token";
        String principalEmail = "test@example.com";
        when(tokenBlackList.isListed(refreshToken)).thenReturn(false);
        when(principal.getName()).thenReturn(principalEmail);
        when(jwtUtil.validateToken(principalEmail, refreshToken)).thenCallRealMethod();
        when(jwtUtil.extractEmail(refreshToken)).thenReturn(principalEmail);
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.refreshToken(refreshToken, principal);
        });

        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }


    @Test
    void testRefreshToken_EmailMisMatch() {
        // Given
        String refreshToken = "expired-refresh-token";
        String principalEmail = "test@example.com";
        when(tokenBlackList.isListed(refreshToken)).thenReturn(false);
        when(principal.getName()).thenReturn(principalEmail);
        when(jwtUtil.validateToken(principalEmail, refreshToken)).thenCallRealMethod();
        when(jwtUtil.extractEmail(refreshToken)).thenReturn("diffrent-email");

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.refreshToken(refreshToken, principal);
        });

        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }
}