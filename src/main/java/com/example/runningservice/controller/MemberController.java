package com.example.runningservice.controller;

import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(
        @RequestBody @Valid SignupRequestDto signupRequestDto) throws Exception {
        // 프로필 이미지 처리
        if (signupRequestDto.getProfileImage() != null && !signupRequestDto.getProfileImage()
            .isEmpty()) {
//            String imageUrl = userService.saveProfileImage(signupRequestDto.getProfileImage());
            // 사용자 정보 저장 시 이미지 URL도 저장
        } else {
//            String defaultImageUrl = userService.getDefaultProfileImageUrl();
            // 사용자 정보 저장 시 기본 이미지 URL 저장
        }

        // 사용자 정보 저장 로직
        return ResponseEntity.ok(memberService.registerUser(signupRequestDto));
    }


}
