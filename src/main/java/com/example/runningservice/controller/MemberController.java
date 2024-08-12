package com.example.runningservice.controller;

import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
//        if (signupRequestDto.getProfileImage() != null && !signupRequestDto.getProfileImage()
//            .isEmpty()) {
////            String imageUrl = userService.saveProfileImage(signupRequestDto.getProfileImage());
//            // 사용자 정보 저장 시 이미지 URL도 저장
//        } else {
////            String defaultImageUrl = userService.getDefaultProfileImageUrl();
//            // 사용자 정보 저장 시 기본 이미지 URL 저장
//        }

        // 사용자 정보 저장 로직
        return ResponseEntity.ok(memberService.signup(signupRequestDto));
    }

    @PostMapping("/signup/email-send")
    ResponseEntity<?> sendVerifyEmail(@RequestParam String email) {
        memberService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/signup/email-verify")
    ResponseEntity<Boolean> verifyUser(@RequestParam String email, @RequestParam String code) {
        return ResponseEntity.ok(memberService.verifyUser(email, code));
    }
}
