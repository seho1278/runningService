package com.example.runningservice.service;

import com.example.runningservice.dto.*;
import com.example.runningservice.client.MailgunClient;
import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailgunClient mailgunClient;
    private final AESUtil aesUtil;

    @Transactional
    public MemberResponseDto signup(SignupRequestDto registerForm) throws Exception {
        // 회원가입 정보 검증
        validateRegisterForm(registerForm);

        // 사용자 엔티티 생성 및 저장(비밀번호와 전화번호는 암호화)
        MemberEntity memberEntity = registerForm.toEntity(passwordEncoder, aesUtil);

        return memberRepository.save(memberEntity).toResponseDto(aesUtil);
    }

    @Transactional
    public void sendEmail(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        //인증코드 생성 및 저장
        String code = getRandomCode();
        memberEntity.saveVerificationCode(code);

        //이메일 전송
        String from = "wadadak@example.com";
        String to = email;
        String subject = "Email 인증메일입니다.";
        String text = getBody(email, memberEntity.getName(), code);
        mailgunClient.sendEmail(from, to, subject, text);
    }

    @Transactional
    public boolean verifyUser(String email, String code) {
        // 이메일로 사용자 찾기
        MemberEntity memberEntity = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 잘못된 인증코드(인증코드 불일치)
        if (!code.equals(memberEntity.getVerificationCode())) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 이메일을 인증된 상태로 표시
        memberEntity.markEmailVerified();

        // 이메일이 인증되었는지를 반환
        return memberEntity.isEmailVerified();
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
  
    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getBody(String email, String name, String code) {

        StringBuilder sb = new StringBuilder();
        sb.append(name).append("님, 안녕하세요!").append("회원가입 완료를 위해 아래 인증 코드를 클릭해주세요.\n\n")
            .append("http://localhost:8080/user/signup/email-verify?email=").append(email)
            .append("&code=").append(code);
        return new String(sb);

    // 사용자 정보 조회
    public MemberResponseDto getMemberProfile(Long user_id) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return MemberResponseDto.of(memberEntity, aesUtil);
    }

    // 사용자 정보 수정
    @Transactional
    public MemberResponseDto updateMemberProfile(
        Long user_id, UpdateMemberRequestDto updateMemberRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        memberEntity.updateMemberProfile(
            updateMemberRequestDto.getNickName(),
            updateMemberRequestDto.getBirthYear(),
            updateMemberRequestDto.getGender(),
            updateMemberRequestDto.getActivityRegion()
            );

        memberRepository.save(memberEntity);

        return MemberResponseDto.of(memberEntity, aesUtil);
    }

    // 비밀번호 변경
    @Transactional
    public void updateMemberPassword(
       Long user_id, PasswordRequestDto passwordRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            if (!passwordEncoder.matches(passwordRequestDto.getOldPassword(), memberEntity.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }

            // 새 비밀번호 확인
            if (!passwordRequestDto.getNewPassword().equals(passwordRequestDto.getConfirmPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }

            // 새 비밀번호 암호화하여 저장
            String encryptedNewPassword = aesUtil.encrypt(passwordRequestDto.getNewPassword());
            memberEntity.updatePassword(encryptedNewPassword);
            memberRepository.save(memberEntity);


        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }
    }
    
    // 사용자 프로필 공개여부 설정
    public void updateProfileVisibility(
        Long user_id, ProfileVisibilityRequestDto profileVisibilityRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        
        // 프로필 공개여부 설정
    }

    // 회원 탈퇴
    public void deleteMember(Long user_id, DeleteRequestDto deleteRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            if (!passwordEncoder.matches(deleteRequestDto.getPassword(), memberEntity.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
            // 회원 탈퇴
            memberRepository.delete(memberEntity);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }
    }
}
