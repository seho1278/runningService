package com.example.runningservice.controller;

import com.example.runningservice.dto.*;
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


    // 사용자 정보 조회
    @GetMapping("/{user_id}/profile")
    public ResponseEntity<MemberResponseDto> getMemberProfile(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(memberService.getMemberProfile(userId));
    }

    // 사용자 정보 수정
    @PutMapping("/{user_id}/profile")
    public ResponseEntity<MemberResponseDto> updateMemberProfile(
        @PathVariable("user_id") Long userId, @RequestBody @Valid UpdateMemberRequestDto updateMemberRequestDto) {
        return ResponseEntity.ok(memberService.updateMemberProfile(userId, updateMemberRequestDto));
    }
    
    // 비밀번호 변경
    @PutMapping("/{user_id}/password")
    public ResponseEntity<?> updateMemberPassword(
        @PathVariable("user_id") Long userId, @RequestBody @Valid PasswordRequestDto passwordRequestDto) {
        memberService.updateMemberPassword(userId, passwordRequestDto);
        return ResponseEntity.ok().build();
    }
    
    //사용자 프로필 공개여부 설정
    @PutMapping("/{user_id}/profile-visibility")
    public ResponseEntity<?> updateMemberProfileVisibility(
        @PathVariable("user_id") Long userId, @RequestBody @Valid ProfileVisibilityRequestDto profileVisibilityRequestDto) {
        memberService.updateProfileVisibility(userId, profileVisibilityRequestDto);
        return ResponseEntity.ok().build();
    }
    
    // 회원 탈퇴
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteMember(@PathVariable("user_id") Long userId, @RequestBody DeleteRequestDto deleteRequestDto) {
        memberService.deleteMember(userId, deleteRequestDto);
        return ResponseEntity.ok().build();
    }

}
