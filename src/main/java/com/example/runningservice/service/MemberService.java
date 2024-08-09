package com.example.runningservice.service;

import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;

    @Transactional
    public MemberResponseDto registerUser(SignupRequestDto registerForm)
            throws Exception {
        // 회원가입 정보 검증
        validateRegisterForm(registerForm);

        // 사용자 엔티티 생성 및 저장(비밀번호와 전화번호는 암호화)
        MemberEntity memberEntity = registerForm.toEntity(passwordEncoder, aesUtil);
        return memberRepository.save(memberEntity).toResponseDto(aesUtil);
    }

    private void validateRegisterForm(SignupRequestDto registerForm) throws Exception {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(registerForm.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 회원 전화번호 중복 체크
        if (memberRepository.existsByPhoneNumber(aesUtil.encrypt(registerForm.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_PHONE);
        }
    }
}
