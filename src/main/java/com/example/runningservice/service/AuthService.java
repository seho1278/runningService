package com.example.runningservice.service;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.LoginRequestDto;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.security.CustomUserDetailsService;
import com.example.runningservice.util.JwtUtil;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final BlackList blackList;

    public JwtResponse authenticate(LoginRequestDto loginRequestDto) throws Exception {
        //loginId와 비밀번호 일치여부 확인 (불일치 시 예외 발생)
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),
                    loginRequestDto.getPassword()));
            log.debug("Authentication successful");
        } catch (AuthenticationException e) {
            throw new CustomException(ErrorCode.INVALID_LOGIN);
        }

        // 해당 이메일로 회원정보 가져오기
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(
            loginRequestDto.getEmail());

        // 회원 권한 가져오기
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        log.debug("Authorities : {}", authorities);
        // 회원권한 & email로 accessToken, refreshToken 발행
        final String accessJwt = jwtUtil.generateToken(userDetails.getUsername(), authorities);
        final String refreshJwt = jwtUtil.generateRefreshToken(userDetails.getUsername(),
            authorities);

        return new JwtResponse(accessJwt, refreshJwt);
    }

    public JwtResponse refreshToken(String refreshToken, Principal principal) {
        //refresh token 이 블랙리스트에 있는지 확인
        if (blackList.isListed(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        //토큰 유효성 검사(올바른 서명 & 유효기간)
        String email = principal.getName();
        if (!jwtUtil.validateToken(email, refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 새로운 accessToken, refreshToken 생성
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        final String newAccessToken = jwtUtil.generateToken(email, authorities);
        final String newRefreshToken = jwtUtil.generateRefreshToken(email, authorities);

        //기존 refreshToken을 blackList에 추가
        blackList.add(refreshToken);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }
}
