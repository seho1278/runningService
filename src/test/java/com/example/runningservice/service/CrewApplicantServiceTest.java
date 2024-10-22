package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantSimpleResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.dto.runProfile.RunProfile;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class CrewApplicantServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private JoinApplicationRepository joinApplicationRepository;

    @Mock
    private ProfileWithRunService profileWithRunService;


    @InjectMocks
    private CrewApplicantService crewApplicantService;

    @Test
    void getAllJoinApplications_Success() {
        // given
        Long crewId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder()
            .status(JoinStatus.PENDING)
            .pageable(PageRequest.of(0, 5, Direction.ASC, "createdAt"))
            .build();

        JoinApplyEntity joinApplyEntity1 = JoinApplyEntity.builder()
            .member(MemberEntity.builder()
                .nickName("testNick1")
                .build())
            .crew(CrewEntity.builder()
                .crewName("testCrew1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0))
                .build())
            .build();
        JoinApplyEntity joinApplyEntity2 = JoinApplyEntity.builder()
            .member(MemberEntity.builder()
                .nickName("testNick2")
                .build())
            .crew(CrewEntity.builder()
                .crewName("testCrew2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
                .build())
            .build();
        Page<JoinApplyEntity> page = new PageImpl<>(List.of(joinApplyEntity1, joinApplyEntity2),
            request.getPageable(),
            2);

        Pageable sortedPageable = PageRequest.of(request.getPageable().getPageNumber(),
            request.getPageable().getPageSize(), Direction.ASC, "createdAt");
        when(joinApplicationRepository.findAllByCrew_IdAndStatus(eq(crewId),
            eq(JoinStatus.PENDING), eq(sortedPageable)))
            .thenReturn(page);

        // when
        Page<CrewApplicantSimpleResponseDto> result = crewApplicantService.getAllJoinApplications(
            crewId,
            request);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("testNick1", result.getContent().get(0).getNickName());
    }

    @Test
    void getJoinApplicationDetail_Success() {
        // given
        Long crewId = 1L;
        Long joinApplyId = 2L;
        Long userId = 1L;

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(joinApplyId)
            .member(MemberEntity.builder().id(userId).nickName("testNick").build())
            .crew(CrewEntity.builder().crewName("testCrew").build())
            .createdAt(LocalDateTime.now())
            .build();

        RunRecordResponseDto runRecordResponseDto = RunRecordResponseDto.builder()
            .runningTime(1000)
            .pace(100)
            .distance(10.1)
            .runCount(10)
            .build();

        RunGoalEntity runGoalEntity = new RunGoalEntity();

        CrewApplicantDetailResponseDto response = CrewApplicantDetailResponseDto.of(joinApplyEntity);
        response.addRunProfile(RunProfile.of(runGoalEntity, runRecordResponseDto));

        when(joinApplicationRepository.findByIdAndCrew_Id(joinApplyId, crewId))
            .thenReturn(Optional.of(joinApplyEntity));
        when(profileWithRunService.getJoinApplicationDetail(joinApplyEntity)).thenReturn(response);

        // when
        CrewApplicantDetailResponseDto result = crewApplicantService.getJoinApplicationDetail(
            crewId, joinApplyId);

        // then
        assertNotNull(result);
        assertEquals("testNick", result.getNickName());
    }

    @Test
    void approveJoinApplication_Success() {
        // given
        Long joinApplyId = 1L;

        MemberEntity memberEntity = MemberEntity.builder()
            .nickName("testNick")
            .profileImageUrl("testImageUrl")
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .birthYearVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
            .phoneNumber("encryptedNumber")
            .name("testName")
            .build();

        CrewEntity crewEntity = CrewEntity.builder()
            .crewName("testCrew")
            .build();

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(joinApplyId)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build();

        CrewMemberEntity newCrewMember = CrewMemberEntity.of(memberEntity, crewEntity);

        when(joinApplicationRepository.findByIdAndStatus(joinApplyId, JoinStatus.PENDING))
            .thenReturn(Optional.of(joinApplyEntity));

        when(crewMemberRepository.save(any(CrewMemberEntity.class)))
            .thenReturn(newCrewMember);

        // when
        CrewMemberEntity result = crewApplicantService.approveJoinApplication(joinApplyId);

        // then
        assertNotNull(result);
        assertEquals(memberEntity.getNickName(), result.getMember().getNickName());
        assertEquals("testImageUrl", result.getMember().getProfileImageUrl());
    }

    @Test
    void rejectJoinApplication_Success() {
        // given
        Long joinApplyId = 1L;

        MemberEntity memberEntity = MemberEntity.builder()
            .nickName("testNick")
            .profileImageUrl("testImageUrl")
            .email("testEmail")
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .birthYearVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
            .phoneNumber("encryptedNumber")
            .name("testName")
            .build();

        CrewEntity crewEntity = CrewEntity.builder()
            .crewName("testCrew")
            .build();

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(joinApplyId)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build();

        when(joinApplicationRepository.findByIdAndStatus(joinApplyId, JoinStatus.PENDING))
            .thenReturn(Optional.of(joinApplyEntity));

        // when
        JoinApplyEntity result = crewApplicantService.rejectJoinApplication(joinApplyId);

        // then
        assertNotNull(result);
        assertEquals(JoinStatus.REJECTED, joinApplyEntity.getStatus());

        verify(joinApplicationRepository).findByIdAndStatus(joinApplyId, JoinStatus.PENDING);
    }
}