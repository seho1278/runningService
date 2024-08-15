package com.example.runningservice.controller;

import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(
        @RequestBody @Valid SignupRequestDto signupRequestDto) throws Exception {
        // 사용자 정보 저장
        return ResponseEntity.ok(signupService.signup(signupRequestDto));
    }

    @PostMapping("/signup/email-send")
    ResponseEntity<?> sendVerifyEmail(@RequestParam String email) {
        signupService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/signup/email-verify")
    ResponseEntity<Boolean> verifyUser(@RequestParam String email, @RequestParam String code) {
        return ResponseEntity.ok(signupService.verifyUser(email, code));
    }
}
