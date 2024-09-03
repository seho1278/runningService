package com.example.runningservice.service;

import com.example.runningservice.dto.member.DeleteRequestDto;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.dto.member.PasswordRequestDto;
import com.example.runningservice.dto.member.UpdateMemberRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.*;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.chat.MessageRepository;
import com.example.runningservice.repository.crewMember.CrewMemberBlackListRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;
    private final S3FileUtil s3FileUtil;
    private final NotificationRepository notificationRepository;
    private final RunGoalRepository runGoalRepository;
    private final RunRecordRepository runRecordRepository;
    private final JoinApplicationRepository joinApplicationRepository;
    private final CrewMemberBlackListRepository crewMemberBlackListRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final ChatJoinRepository chatJoinRepository;
    private final MessageRepository messageRepository;

    // 사용자 정보 조회
    @Transactional
    public MemberResponseDto getMemberProfile(Long memberId) {
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);
        return MemberResponseDto.of(memberEntity, aesUtil, s3FileUtil);
    }

    // 사용자 정보 수정
    @Transactional
    public MemberResponseDto updateMemberProfile(Long memberId,
                                                 UpdateMemberRequestDto updateMemberRequestDto) {
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);

        // 프로필 이미지 업로드
        profileImageUploadHandler(updateMemberRequestDto.getProfileImage(), memberId, memberEntity);

        memberEntity.updateMemberProfile(updateMemberRequestDto);

        memberRepository.save(memberEntity);

        return MemberResponseDto.of(memberEntity, aesUtil, s3FileUtil);
    }

    // 비밀번호 변경
    @Transactional
    public void updateMemberPassword(Long memberId, PasswordRequestDto passwordRequestDto) {
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            validateOldPassword(passwordRequestDto.getOldPassword(), memberEntity.getPassword());

            // 새 비밀번호 암호화하여 저장
            String encryptedNewPassword = passwordEncoder.encode(passwordRequestDto.getNewPassword());
            memberEntity.updatePassword(encryptedNewPassword);
            memberRepository.save(memberEntity);


        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMember(Long memberId, DeleteRequestDto deleteRequestDto) {
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);

        try {
            // 저장된 비밀번호화 입력한 oldPassword가 일치하는지 확인
            validateOldPassword(deleteRequestDto.getPassword(), memberEntity.getPassword());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_ERROR);
        }

        // 사용자 알람 제거

        // 사용자 목표 제거
        runGoalRepository.deleteAllByUserId_Id(memberId);

        // 사용자 기록 제거
        runRecordRepository.deleteAllByUserId_Id(memberId);

        // 크루 가입 상태 확인
        List<CrewMemberEntity> crewMemberEntities = crewMemberRepository.findAllByMember_Id(memberId);
        if (!crewMemberEntities.isEmpty()){
            // 가입되어있는 경우
            // 크루 리더인지 확인
            crewMemberEntities.stream()
                .filter(crewMemberEntity -> crewMemberEntity.getRole() == CrewRole.LEADER)
                .findAny()
                .ifPresent(crewMemberEntity -> {
                    throw new RuntimeException("크루 리더 위임 후 탈퇴해주세요");
                });

            // 참여중인 채팅방 확인
            List<ChatJoinEntity> chatJoinEntities = chatJoinRepository.findAllByMember_Id(memberId);

            if (!chatJoinEntities.isEmpty()){
                // 참여중인 채팅방의 메시지 연결 끊기
                chatJoinEntities.stream()
                    .flatMap(chatJoinEntity -> messageRepository.findAllByChatJoin(chatJoinEntity).stream())
                    .forEach(message -> message.setChatJoinNull(null));

                // 참여중인 채팅방 퇴장
                chatJoinRepository.deleteAllByMember_Id(memberId);
            }

            joinApplicationRepository.deleteAllByMember_Id(memberId);
            crewMemberBlackListRepository.deleteAllByMember_Id(memberId);
            crewMemberRepository.deleteAllByMember_Id(memberId);
        }

        // 회원 탈퇴
        memberRepository.delete(memberEntity);
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

