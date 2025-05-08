package com.example.runningservice.service;

import com.example.runningservice.dto.auth.AdditionalInfoRequestDto;
import com.example.runningservice.dto.auth.JwtResponse;
import com.example.runningservice.dto.auth.Oauth2DataDto;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Role;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Oauth2ProcessService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AESUtil aesUtil;

    @Transactional
    public JwtResponse processOauth2Info(GoogleAccountProfileResponseDto googleProfile,
        HttpServletResponse response) throws IOException {
        String email = googleProfile.getEmail();
        String name = googleProfile.getName();
        String profileImage = googleProfile.getPicture();

        MemberEntity memberEntity = memberRepository.findByEmail(googleProfile.getEmail())
            .orElseGet(
                () -> memberRepository.save(memberRepository.save(MemberEntity.builder()
                    .email(email)
                    .name(name)
                    .profileImageUrl(profileImage)
                    .roles(new ArrayList<>(List.of(Role.ROLE_USER)))
                    .build())));
            if (!isInfoComplete(memberEntity)) {

            log.info("소셜 계정 회원가입 요청 후 필수정보가 누락된 상태로 저장되었습니다");

            String redirectUrl = String.format("/user/signup/saved-info?email=%s",
                URLEncoder.encode(memberEntity.getEmail(), StandardCharsets.UTF_8));
            response.sendRedirect(redirectUrl);
            response.sendRedirect("/user/signup/saved-info");
            return null;
            }
        //필수정보가 다 채워져있으면 토큰 발행
        return getJwtResponse(memberEntity);
    }

    @Transactional
    public Oauth2DataDto getOauthData(String email) {
        // 이메일을 통해 기존 회원 정보를 가져옴
        MemberEntity memberEntity = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // DTO 초기화 및 기본 값 설정
        return Oauth2DataDto.builder()
            .email(email)
            .name(memberEntity.getName())
            .image(memberEntity.getProfileImageUrl())
            .build();
    }

    @Transactional
    public JwtResponse completeSignup(AdditionalInfoRequestDto form) {
        // 기존 회원 정보 업데이트
        MemberEntity memberEntity = memberRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        memberEntity.updateAdditionalInfo(form, aesUtil);

        return getJwtResponse(memberEntity);
    }

    private JwtResponse getJwtResponse(MemberEntity memberEntity) {
        List<GrantedAuthority> authorities = memberEntity.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name())).collect(
                Collectors.toList());

        String accessToken = jwtUtil.generateToken(memberEntity.getEmail(), memberEntity.getId(),
            authorities);
        String refreshToken = jwtUtil.generateRefreshToken(memberEntity.getEmail(),
            memberEntity.getId(), authorities);

        return JwtResponse.builder()
            .accessJwt(accessToken)
            .refreshJwt(refreshToken)
            .build();
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
