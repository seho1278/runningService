package com.example.runningservice.service;

import com.example.runningservice.dto.*;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.dto.member.PasswordRequestDto;
import com.example.runningservice.dto.member.ProfileVisibilityRequestDto;
import com.example.runningservice.dto.member.UpdateMemberRequestDto;
import com.example.runningservice.entity.MemberEntity;
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
    public MemberResponseDto getMemberProfile(Long user_id) throws Exception {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return MemberResponseDto.of(memberEntity, aesUtil);
    }

    // 사용자 정보 수정
    @Transactional
    public MemberResponseDto updateMemberProfile(
        Long user_id, UpdateMemberRequestDto updateMemberRequestDto) throws Exception {

        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 프로필 이미지가 있는 경우
        MultipartFile profileImage = updateMemberRequestDto.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = "user-" + user_id;
            s3FileUtil.putObject(fileName, profileImage);
            String imageUrl = s3FileUtil.getImgUrl(fileName);
            memberEntity.updateProfileImageUrl(imageUrl);
        }

        memberEntity.updateMemberProfile(updateMemberRequestDto.getNickName(),
            updateMemberRequestDto.getBirthYear(), updateMemberRequestDto.getGender(),
            updateMemberRequestDto.getActivityRegion());

        memberRepository.save(memberEntity);

        return MemberResponseDto.of(memberEntity, aesUtil);
    }

    // 비밀번호 변경
    @Transactional
    public void updateMemberPassword(Long user_id, PasswordRequestDto passwordRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            if (!passwordEncoder.matches(passwordRequestDto.getOldPassword(),
                memberEntity.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }

            // 새 비밀번호 확인
            if (!passwordRequestDto.getNewPassword()
                .equals(passwordRequestDto.getConfirmPassword())) {
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
    public ProfileVisibilityResponseDto updateProfileVisibility(Long user_id,
                                                                ProfileVisibilityRequestDto profileVisibilityRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 프로필 공개여부 설정
        memberEntity.updateProfileVisibility(
            profileVisibilityRequestDto.getUserName(),
            profileVisibilityRequestDto.getPhoneNumber(),
            profileVisibilityRequestDto.getGender(),
            profileVisibilityRequestDto.getBirthYear());

        memberRepository.save(memberEntity);

        return ProfileVisibilityResponseDto.of(memberEntity);
    }

    // 회원 탈퇴
    public void deleteMember(Long user_id, DeleteRequestDto deleteRequestDto) {
        MemberEntity memberEntity = memberRepository.findById(user_id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            if (!passwordEncoder.matches(deleteRequestDto.getPassword(),
                memberEntity.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
            // 회원 탈퇴
            memberRepository.delete(memberEntity);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }
    }
}

