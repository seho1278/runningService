package com.example.runningservice.controller;

import com.example.runningservice.dto.member.*;
import com.example.runningservice.service.MemberService;
import com.example.runningservice.util.LoginUser;
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

    // 사용자 정보 조회
    @GetMapping("/{user_id}/profile")
    public ResponseEntity<MemberResponseDto> getMemberProfile(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(memberService.getMemberProfile(userId));
    }

    // 사용자 정보 수정
    @PutMapping("/{user_id}/profile")
    public ResponseEntity<MemberResponseDto> updateMemberProfile(
        @PathVariable("user_id") @LoginUser Long userId, @RequestBody @Valid UpdateMemberRequestDto updateMemberRequestDto) {
        return ResponseEntity.ok(memberService.updateMemberProfile(userId, updateMemberRequestDto));
    }

    // 비밀번호 변경
    @PutMapping("/{user_id}/password")
    public ResponseEntity<Void> updateMemberPassword(
        @PathVariable("user_id") @LoginUser Long userId, @RequestBody @Valid PasswordRequestDto passwordRequestDto) {
        memberService.updateMemberPassword(userId, passwordRequestDto);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("user_id") @LoginUser Long userId, @RequestBody DeleteRequestDto deleteRequestDto) {
        memberService.deleteMember(userId, deleteRequestDto);
        return ResponseEntity.ok().build();
    }
}
