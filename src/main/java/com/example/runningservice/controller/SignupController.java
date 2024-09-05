package com.example.runningservice.controller;

import com.example.runningservice.dto.auth.SignupRequestDto;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class SignupController {

    private final SignupService signupService;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(
        @ModelAttribute @Valid SignupRequestDto signupRequestDto) throws Exception {
        // 사용자 정보 저장
        return ResponseEntity.ok(signupService.signup(signupRequestDto));
    }

    @PostMapping("/signup/email-send")
    ResponseEntity<Void> sendVerifyEmail(@RequestParam String email) {
        signupService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/signup/email-verify")
    ResponseEntity<Void> verifyUser(@RequestParam String email, @RequestParam String code) {
        signupService.verifyUser(email, code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/signup/nickname-verify")
    ResponseEntity<Void> verifyNickname(@RequestParam String nickname) {

        signupService.verifyNickName(nickname);
        return ResponseEntity.ok().build();
    }
}
