package com.example.runningservice.service;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Role;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Oauth2CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final OAuth2Service oAuth2Service;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String code = oauthUser.getAttribute("code");

        // OAuth2Service를 사용하여 Google 프로필 정보를 가져옴
        GoogleAccountProfileResponseDto profile = oAuth2Service.getGoogleAccountProfile(code);
        //email, name, 이미지 받기
        String email = profile.getEmail();
        String name = profile.getName();
        String image = profile.getPicture();

        MemberEntity memberEntity = memberRepository.findByEmail(profile.getEmail())
            .orElseGet(
                () -> memberRepository.save(memberRepository.save(MemberEntity.builder()
                    .email(email)
                    .name(name)
                    .profileImageUrl(image)
                    .roles(new ArrayList<>(List.of(Role.ROLE_USER)))
                    .build())));

        // 추가 정보 입력이 필요한 경우 리디렉션
        if (!isInfoComplete(memberEntity)) {
            response.sendRedirect("/additional-info?email=" + email);
            return;
        }
        // 필수정보가 모두 있을 시, 토큰 발행(로그인 완료)
        List<GrantedAuthority> authorities = memberEntity.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());

        String accessToken = jwtUtil.generateToken(memberEntity.getEmail(), memberEntity.getId(),
            authorities);

        String refreshToken = jwtUtil.generateRefreshToken(memberEntity.getEmail(),
            memberEntity.getId(), authorities);

        JwtResponse jwtResponse = JwtResponse.builder().accessJwt(accessToken)
            .refreshJwt(refreshToken).build();

        // 응답 타입을 JSON으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 응답으로 토큰 전송
        response.getWriter().write(objectMapper.writeValueAsString(jwtResponse));
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // 이 메서드에서 위의 onAuthenticationSuccess 메서드를 호출하고 체인을 통해 다음 필터로 넘김
        this.onAuthenticationSuccess(request, response, authentication);
        chain.doFilter(request, response);
    }

    private boolean isInfoComplete(MemberEntity member) {
        // 필수 정보가 모두 있는지 검증
        return member.getName() != null &&
            member.getPhoneNumber() != null &&
            member.getBirthYear() != null &&
            member.getActivityRegion() != null &&
            member.getNickName() != null &&
            member.getPhoneNumberVisibility() != null &&
            member.getBirthYearVisibility() != null &&
            member.getGenderVisibility() != null &&
            member.getNameVisibility() != null;
    }
}
