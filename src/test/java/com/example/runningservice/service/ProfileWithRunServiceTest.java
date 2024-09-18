package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.crewMember.CrewMemberResponseDetailDto;
import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.util.AESUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileWithRunServiceTest {

    @Mock
    private RunGoalRepository runGoalRepository;

    @Mock
    private RunRecordService runRecordService;

    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private ProfileWithRunService profileWithRunService;

    @Test
    void getCrewMemberWithEntity_success() {
        //given
        Long userId = 1L;
        Long crewId = 1L;

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .id(1L)
            .member(MemberEntity.builder().id(userId).nickName("testNick").build())
            .crew(CrewEntity.builder().id(crewId).crewName("crew1").build())
            .build();

        RunGoalEntity runGoalEntity = RunGoalEntity.builder()
            .runCount(10)
            .totalDistance(10.5)
            .totalRunningTime(10000)
            .averagePace(1000)
            .build();

        RunRecordResponseDto runRecordResponseDto = RunRecordResponseDto.builder()
            .runCount(10)
            .runningTime(10000)
            .distance(10.0)
            .pace(1000)
            .build();

        when(runGoalRepository.findFirstByUserId_IdOrderByCreatedAtDesc(userId)).thenReturn(
            Optional.of(runGoalEntity));
        when(runRecordService.calculateTotalRunRecords(userId)).thenReturn(runRecordResponseDto);

        //when
        CrewMemberResponseDetailDto result = profileWithRunService.getCrewMemberWithEntity(
            crewMember);

        //then
        assertEquals(10.0, result.getRunProfile().getTotalDistance());
        assertEquals("testNick", result.getMemberNickName());
    }


    @Test
    void getJoinApplicantWithEntity_success() {
        //given
        Long userId = 1L;
        Long crewId = 1L;

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(MemberEntity.builder().id(userId).nickName("testNick").build())
            .crew(CrewEntity.builder().id(crewId).crewName("crew1").build())
            .build();

        RunGoalEntity runGoalEntity = RunGoalEntity.builder()
            .runCount(10)
            .totalDistance(10.5)
            .totalRunningTime(10000)
            .averagePace(1000)
            .build();

        RunRecordResponseDto runRecordResponseDto = RunRecordResponseDto.builder()
            .runCount(10)
            .runningTime(10000)
            .distance(10.0)
            .pace(1000)
            .build();

        when(runGoalRepository.findFirstByUserId_IdOrderByCreatedAtDesc(userId)).thenReturn(
            Optional.of(runGoalEntity));
        when(runRecordService.calculateTotalRunRecords(userId)).thenReturn(runRecordResponseDto);

        //when
        CrewApplicantDetailResponseDto result = profileWithRunService.getJoinApplicationDetail(
            joinApplyEntity);

        //then
        assertEquals(10.0, result.getRunProfile().getTotalDistance());
        assertEquals("testNick", result.getNickName());
    }
}