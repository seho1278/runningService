package com.example.runningservice.service;

import com.example.runningservice.dto.member.*;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;
    private final S3FileUtil s3FileUtil;

    // 사용자 정보 조회
    @Transactional
    public MemberResponseDto getMemberProfile(Long userId) {
        MemberEntity memberEntity = memberRepository.findMemberById(userId);
        return MemberResponseDto.of(memberEntity, aesUtil);
    }

    // 사용자 정보 수정
    @Transactional
    public MemberResponseDto updateMemberProfile(Long userId,
                                                 UpdateMemberRequestDto updateMemberRequestDto) {
        MemberEntity memberEntity = memberRepository.findMemberById(userId);

        // 프로필 이미지 업로드
        profileImageUploadHandler(updateMemberRequestDto.getProfileImage(), userId, memberEntity);

        memberEntity.updateMemberProfile(updateMemberRequestDto);

        memberRepository.save(memberEntity);

        return MemberResponseDto.of(memberEntity, aesUtil);
    }

    // 비밀번호 변경
    @Transactional
    public void updateMemberPassword(Long userId, PasswordRequestDto passwordRequestDto) {
        MemberEntity memberEntity = memberRepository.findMemberById(userId);

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            validateOldPassword(passwordRequestDto.getOldPassword(), memberEntity.getPassword());

            // 새 비밀번호 암호화하여 저장
            String encryptedNewPassword = aesUtil.encrypt(passwordRequestDto.getNewPassword());
            memberEntity.updatePassword(encryptedNewPassword);
            memberRepository.save(memberEntity);


        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMember(Long userId, DeleteRequestDto deleteRequestDto) {
        MemberEntity memberEntity = memberRepository.findMemberById(userId);

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            validateOldPassword(deleteRequestDto.getPassword(), memberEntity.getPassword());

            // 회원 탈퇴
            memberRepository.delete(memberEntity);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }
    }

    // 이미지 업데이트
    private void profileImageUploadHandler(
        MultipartFile profileImage, Long userId, MemberEntity memberEntity) {
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = "user-" + userId;
            s3FileUtil.putObject(fileName, profileImage);
            String imageUrl = s3FileUtil.getImgUrl(fileName);
            memberEntity.updateProfileImageUrl(imageUrl);
        }
    }

    // 기존 비밀번호 확인
    private void validateOldPassword(String oldPassword, String storedPassword) {
        if (!passwordEncoder.matches(oldPassword, storedPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

}

