package com.example.runningservice.service;

import com.example.runningservice.client.MailgunClient;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MailgunClient mailgunClient;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;
    private final MemberRepository memberRepository;
    private final S3FileUtil s3FileUtil;

    @Transactional
    public MemberResponseDto signup(SignupRequestDto registerForm) throws Exception {
        // 회원가입 정보 검증
        validateRegisterForm(registerForm);

        // 사용자 엔티티 생성 및 저장(비밀번호와 전화번호는 암호화)
        MemberEntity memberEntity = registerForm.toEntity(passwordEncoder, aesUtil);

        //imageUrl을 엔티티에 저장
        String imageUrl = uploadFileAndReturnFileName(memberEntity.getId(),
            registerForm.getProfileImage());
        memberEntity.updateProfileImageUrl(imageUrl);

        return MemberResponseDto.of(memberRepository.save(memberEntity), aesUtil);
    }

    @Transactional
    public void sendEmail(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        //인증코드 생성 및 저장
        String code = getRandomCode();
        memberEntity.saveVerificationCode(code);

        //이메일 전송
        String from = "simzoo93@naver.com";
        String to = email;
        String subject = "Email 인증메일입니다.";
        String text = getBody(email, memberEntity.getName(), code);
        mailgunClient.sendEmail(from, to, subject, text);
    }

    @Transactional
    public void verifyUser(String email, String code) {
        // 이메일로 사용자 찾기
        MemberEntity memberEntity = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 잘못된 인증코드(인증코드 불일치)
        if (!code.equals(memberEntity.getVerificationCode())) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 이메일을 인증된 상태로 표시
        memberEntity.markEmailVerified();
    }

    private void validateRegisterForm(SignupRequestDto registerForm) throws Exception {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(registerForm.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 닉네임 중복 체크
        if (memberRepository.existsByNickName(registerForm.getNickName())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_NICKNAME);
        }
        // 휴대전화번호 중복 체크
        if (memberRepository.existsByPhoneNumberHash(
            aesUtil.generateHash(registerForm.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_PHONE);
        }
    }

    //랜덤코드 생성
    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    // 이메일 텍스트 생성
    private String getBody(String email, String name, String code) {

        StringBuilder sb = new StringBuilder();
        sb.append(name).append("님, 안녕하세요!").append("회원가입 완료를 위해 아래 인증 코드를 클릭해주세요.\n\n")
            .append("http://13.209.127.192:8080/user/signup/email-verify?email=").append(email)
            .append("&code=").append(code);
        return new String(sb);
    }

    /**
     * s3에 회원 이미지 저장 :: user-{userId}로 저장
     */
    private String uploadFileAndReturnFileName(Long userId, MultipartFile userImage) {
        if (userImage != null) {
            String fileName = "user-" + userId;
            s3FileUtil.putObject(fileName, userImage);

            return s3FileUtil.getImgUrl(fileName);
        } else { // 크루 이미지가 없으면 기본 이미지로 사용
            return s3FileUtil.getImgUrl("user-default");
        }
    }

    @Transactional
    public void verifyNickName(String nickname) {
        if (memberRepository.existsByNickName(nickname)) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_NICKNAME);
        }
    }

    @Transactional
    public MemberResponseDto saveAdditionalInfo(SignupRequestDto form) {
        // 기존 회원 정보 업데이트
        MemberEntity memberEntity = memberRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        memberEntity.updateAdditionalInfo(form, aesUtil);

        return MemberResponseDto.of(memberRepository.save(memberEntity), aesUtil);
    }
}
