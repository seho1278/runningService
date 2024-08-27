package com.example.runningservice.controller;

import com.example.runningservice.dto.Oauth2DataDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
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
    ResponseEntity<?> verifyNickname(@RequestParam String nickname) {

        signupService.verifyNickName(nickname);
        return ResponseEntity.ok().build();
    }

    //필수정보 확인
    //채워지지 않은 필수정보 입력하도록 함
    @GetMapping("/additional-info")
    public ResponseEntity<Oauth2DataDto> showAdditionalInfoForm(@RequestParam("email") String email) {
        // 이메일을 통해 기존 회원 정보를 가져옴
        MemberEntity memberEntity = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // DTO 초기화 및 기본 값 설정
        Oauth2DataDto form = Oauth2DataDto.builder()
            .email(email)
            .name(memberEntity.getName())
            .image(memberEntity.getProfileImageUrl())
            .build();

        return ResponseEntity.ok(form);
    }

    @PostMapping("/additional-info")
    public ResponseEntity<?> processAdditionalInfo(@ModelAttribute @Valid SignupRequestDto form) {

        return ResponseEntity.ok(signupService.saveAdditionalInfo(form));
    }
}
