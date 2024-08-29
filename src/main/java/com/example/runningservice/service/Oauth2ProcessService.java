package com.example.runningservice.service;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Role;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.JwtUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Oauth2ProcessService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public JwtResponse processOauth2Info(GoogleAccountProfileResponseDto googleProfile) {

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
            throw new CustomException(ErrorCode.MISSING_REQUIRED_INFORMATION);
        }

        List<GrantedAuthority> authorities = memberEntity.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name())).collect(
                Collectors.toList());

        String accessToken = jwtUtil.generateToken(memberEntity.getEmail(), memberEntity.getId(),
            authorities);
        String refreshToken = jwtUtil.generateRefreshToken(memberEntity.getEmail(),
            memberEntity.getId(), authorities);
        JwtResponse jwtResponse = JwtResponse.builder()
            .accessJwt(accessToken)
            .refreshJwt(refreshToken)
            .build();

        return jwtResponse;
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
