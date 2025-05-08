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
    @GetMapping("/{member_id}/profile")
    public ResponseEntity<MemberResponseDto> getMemberProfile(@PathVariable("member_id") Long memberId) {
        return ResponseEntity.ok(memberService.getMemberProfile(memberId));
    }

    // 사용자 정보 수정
    @PutMapping("/profile")
    public ResponseEntity<MemberResponseDto> updateMemberProfile(
        @LoginUser Long memberId, @Valid UpdateMemberRequestDto updateMemberRequestDto) {
        return ResponseEntity.ok(memberService.updateMemberProfile(memberId, updateMemberRequestDto));
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<Void> updateMemberPassword(
        @LoginUser Long memberId, @RequestBody @Valid PasswordRequestDto passwordRequestDto) {
        memberService.updateMemberPassword(memberId, passwordRequestDto);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("")
    public ResponseEntity<Void> deleteMember(@LoginUser Long memberId, @RequestBody DeleteRequestDto deleteRequestDto) {
        memberService.deleteMember(memberId, deleteRequestDto);
        return ResponseEntity.ok().build();
    }
}
