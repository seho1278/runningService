package com.example.runningservice.service;

import com.example.runningservice.dto.crewMember.CrewMemberResponseDetailDto;
import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.runProfile.RunProfile;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileWithRunService {

    private final AESUtil aesUtil;
    private final RunGoalRepository runGoalRepository;
    private final RunRecordService runRecordService;

    /**
     * entity를 인자로 받아 crewMember 가져오기
     */
    @Transactional
    public CrewMemberResponseDetailDto getCrewMemberWithEntity(CrewMemberEntity crewMemberEntity) {

        MemberEntity memberEntity = crewMemberEntity.getMember();
        if (memberEntity.getRunProfileVisibility() == Visibility.PRIVATE) {
            return CrewMemberResponseDetailDto.of(crewMemberEntity, aesUtil);
        }

        Long userId = memberEntity.getId();
        //ResponseDto 생성
        CrewMemberResponseDetailDto crewMemberResponseDetailDto = CrewMemberResponseDetailDto.of(
            crewMemberEntity, aesUtil);
        //RunGoal 확인(없을 시 Null로 생성)
        RunGoalEntity runGoalEntity = runGoalRepository.findFirstByUserId_IdOrderByCreatedAtDesc(
            userId).orElseGet(RunGoalEntity::new);

        RunRecordResponseDto runRecordResponseDto = runRecordService.calculateTotalRunRecords(
            userId);

        crewMemberResponseDetailDto.addRunProfile(
            RunProfile.of(runGoalEntity, runRecordResponseDto));

        return crewMemberResponseDetailDto;
    }

    @Transactional
    public CrewApplicantDetailResponseDto getJoinApplicationDetail(JoinApplyEntity joinApplyEntity) {

        MemberEntity memberEntity = joinApplyEntity.getMember();
        if (memberEntity.getRunProfileVisibility() == Visibility.PRIVATE) {
            return CrewApplicantDetailResponseDto.of(joinApplyEntity);
        }

        Long userId = memberEntity.getId();
        //RunGoal 확인(없을 시 Null로 생성)
        RunGoalEntity runGoalEntity = runGoalRepository.findFirstByUserId_IdOrderByCreatedAtDesc(
            userId).orElseGet(RunGoalEntity::new);

        RunRecordResponseDto runRecordResponseDto = runRecordService.calculateTotalRunRecords(
            userId);

        CrewApplicantDetailResponseDto crewApplicantDetailResponseDto = CrewApplicantDetailResponseDto.of(
            joinApplyEntity);

        crewApplicantDetailResponseDto.addRunProfile(
            RunProfile.of(runGoalEntity, runRecordResponseDto));
        return crewApplicantDetailResponseDto;
    }
}
