package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.activity.ActivityFilterDto;
import com.example.runningservice.dto.activity.ActivityRequestDto;
import com.example.runningservice.dto.activity.ActivityRequestDto.Update;
import com.example.runningservice.dto.activity.ActivityResponseDto;
import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.ParticipantEntity;
import com.example.runningservice.entity.RegularRunMeetingEntity;
import com.example.runningservice.enums.ActivityCategory;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private CrewRepository crewRepository;
    @Mock
    private CrewMemberRepository crewMemberRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RegularRunMeetingRepository regularRunMeetingRepository;
    @InjectMocks
    private ActivityService activityService;

    @Test
    @DisplayName("정기러닝 생성_권한 O")
    public void createRegularActivity_Authorized() {
        // given
        Long userId = 1L;
        Long crewId = 10L;
        Long regularId = 22L;

        ActivityRequestDto.Create activityDto = mock(ActivityRequestDto.Create.class);
        when(activityDto.getRegularId()).thenReturn(regularId);

        CrewMemberEntity crewMemberEntity = CrewMemberEntity.builder()
            .role(CrewRole.LEADER)
            .build();
        RegularRunMeetingEntity regularEntity = RegularRunMeetingEntity.builder()
            .id(regularId)
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(mock(CrewEntity.class)));
        given(memberRepository.findById(userId)).willReturn(Optional.of(mock(MemberEntity.class)));
        given(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, userId)).willReturn(
            Optional.of(crewMemberEntity));
        given(regularRunMeetingRepository.findById(activityDto.getRegularId())).willReturn(
            Optional.of(regularEntity));

        // when
        ActivityResponseDto response = activityService.createRegularActivity(userId, crewId,
            activityDto);

        // then
        assertEquals(response.getRegularId(), regularId);
        assertEquals(response.getCategory(), ActivityCategory.REGULAR);
        assertEquals(response.getParticipant(), 0);
    }

    @Test
    @DisplayName("정기러닝 생성_권한 X")
    void createRegularActivity_NotAuthorized() {
        // given
        Long userId = 1L;
        Long crewId = 10L;

        ActivityRequestDto.Create activityDto = mock(ActivityRequestDto.Create.class);

        CrewMemberEntity crewMemberEntity = CrewMemberEntity.builder()
            .role(CrewRole.MEMBER)
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(mock(CrewEntity.class)));
        given(memberRepository.findById(userId)).willReturn(Optional.of(mock(MemberEntity.class)));
        given(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, userId)).willReturn(
            Optional.of(crewMemberEntity));

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
            activityService.createRegularActivity(userId, crewId, activityDto)
        );

        // then
        assertEquals(ErrorCode.UNAUTHORIZED_REGULAR_ACCESS, exception.getErrorCode());
    }

    @Test
    @DisplayName("번개러닝 생성")
    void createOnDemandActivity() {
        // given
        Long userId = 1L;
        Long crewId = 10L;

        ActivityRequestDto.Create activityDto = mock(ActivityRequestDto.Create.class);

        MemberEntity memberEntity = MemberEntity.builder().build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(mock(CrewEntity.class)));
        given(memberRepository.findById(userId)).willReturn(Optional.of(memberEntity));

        // when
        ActivityResponseDto response = activityService.createOnDemandActivity(userId, crewId,
            activityDto);

        // then
        assertNull(response.getRegularId());
        assertEquals(response.getCategory(), ActivityCategory.ON_DEMAND);
        assertEquals(response.getParticipant(), 0);
    }

    @Test
    @DisplayName("정기러닝 일정 수정_권한 O")
    void updateActivity_Regular_Authorized() {
        // given
        Long userId = 1L;
        Long crewId = 10L;
        Long activityId = 50L;
        Long regularId = 22L;

        ActivityRequestDto.Update activityDto = mock(Update.class);
        when(activityDto.getDate()).thenReturn(LocalDate.of(2024, 1, 1));

        MemberEntity memberEntity = MemberEntity.builder().id(userId).nickName("nick").build();
        ActivityEntity activityEntity = ActivityEntity.builder()
            .regularRun(RegularRunMeetingEntity.builder()
                .id(regularId)
                .build())
            .author(memberEntity)
            .build();
        CrewMemberEntity crewMemberEntity = CrewMemberEntity.builder()
            .role(CrewRole.LEADER)
            .build();

        given(activityRepository.findById(activityId)).willReturn(Optional.of(activityEntity));
        given(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, userId)).willReturn(
            Optional.of(crewMemberEntity));

        // when
        ActivityResponseDto response = activityService.updateActivity(userId, crewId, activityId,
            activityDto);

        // then
        assertEquals(response.getCategory(), ActivityCategory.REGULAR);
        assertEquals(response.getDate(), LocalDate.of(2024, 1, 1));
        assertEquals(response.getRegularId(), regularId);
        assertEquals(response.getAuthor(), memberEntity.getNickName());
    }

    @Test
    @DisplayName("번개러닝 일정 수정_권한 O")
    void updateActivity_ON_DEMAND_Authorized() {
        // given
        Long userId = 1L;
        Long crewId = 10L;
        Long activityId = 50L;

        ActivityRequestDto.Update activityDto = mock(Update.class);
        when(activityDto.getDate()).thenReturn(LocalDate.of(2024, 1, 1));

        MemberEntity memberEntity = MemberEntity.builder().id(userId).nickName("nick").build();
        ActivityEntity activityEntity = ActivityEntity.builder()
            .author(memberEntity)
            .build();

        given(activityRepository.findById(activityId)).willReturn(Optional.of(activityEntity));

        // when
        ActivityResponseDto response = activityService.updateActivity(userId, crewId, activityId,
            activityDto);

        // then
        assertEquals(response.getCategory(), ActivityCategory.ON_DEMAND);
        assertEquals(response.getDate(), LocalDate.of(2024, 1, 1));
        assertEquals(response.getAuthor(), memberEntity.getNickName());
        verify(crewMemberRepository, never()).findByCrew_IdAndMember_Id(any(), any());
    }

    @Test
    @DisplayName("번개러닝 일정 수정_권한 X")
    void updateActivity_ON_DEMAND_NotAuthorized() {
        // given
        Long userId = 1L;
        Long authorId = 2L;
        Long crewId = 10L;
        Long activityId = 50L;

        ActivityRequestDto.Update activityDto = mock(Update.class);

        MemberEntity memberEntity = MemberEntity.builder().id(authorId).nickName("nick").build();
        ActivityEntity activityEntity = ActivityEntity.builder()
            .author(memberEntity)
            .build();

        given(activityRepository.findById(activityId)).willReturn(Optional.of(activityEntity));

        // when
        CustomException customException = assertThrows(CustomException.class,
            () -> activityService.updateActivity(userId, crewId, activityId, activityDto));

        // then
        assertEquals(customException.getErrorCode(), ErrorCode.UNAUTHORIZED_ACTIVITY);
    }

    @Test
    @DisplayName("번개러닝 삭제_리더")
    void deleteActivity_Leader() {
        // given
        Long userId = 1L;
        Long authorId = 99L;
        Long crewId = 2L;
        Long activityId = 3L;

        MemberEntity authorEntity = MemberEntity.builder().id(authorId).nickName("nick").build();

        given(activityRepository.findById(activityId)).willReturn(
            Optional.of(ActivityEntity.builder().author(authorEntity).build()));
        given(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, userId)).willReturn(
            Optional.of(CrewMemberEntity.builder().role(CrewRole.LEADER).build()));

        // when
        ActivityResponseDto response = activityService.deleteActivity(userId, crewId, activityId);

        // then
        assertEquals(response.getAuthor(), authorEntity.getNickName());
    }

    @Test
    @DisplayName("번개러닝 삭제_크루원")
    void deleteActivity_CrewMember() {
        // given
        Long userId = 1L;
        Long crewId = 2L;
        Long activityId = 3L;

        MemberEntity authorEntity = MemberEntity.builder().id(userId).nickName("nick").build();

        given(activityRepository.findById(activityId)).willReturn(
            Optional.of(ActivityEntity.builder().author(authorEntity).build()));
        given(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, userId)).willReturn(
            Optional.of(CrewMemberEntity.builder().role(CrewRole.MEMBER).build()));

        // when
        ActivityResponseDto response = activityService.deleteActivity(userId, crewId, activityId);

        // then
        assertEquals(response.getAuthor(), authorEntity.getNickName());
    }

    @Test
    @DisplayName("특정 (정기)러닝 조회")
    void getActivity() {
        // given
        Long activityId = 1L;
        List<ParticipantEntity> participants = List.of(
            mock(ParticipantEntity.class), mock(ParticipantEntity.class));
        MemberEntity memberEntity = MemberEntity.builder().nickName("nick").build();
        ActivityEntity activityEntity = ActivityEntity.builder()
            .participant(participants)
            .author(memberEntity)
            .regularRun(mock(RegularRunMeetingEntity.class))
            .build();
        given(activityRepository.findById(activityId)).willReturn(Optional.of(activityEntity));

        // when
        ActivityResponseDto response = activityService.getActivity(activityId);

        // then
        assertEquals(response.getParticipant(), participants.size());
        assertEquals(response.getCategory(), ActivityCategory.REGULAR);
    }

    @Test
    @DisplayName("크루 러닝 조회_날짜 검증 실패")
    void getCrewActivity_FailedValidateDate() {
        // given
        Long crewId = 1L;
        ActivityFilterDto wrongDate = ActivityFilterDto.builder()
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().minusDays(1))
            .build();

        // when
        CustomException customException = assertThrows(CustomException.class,
            () -> activityService.getCrewActivityByDate(crewId, wrongDate, Pageable.ofSize(1)));

        // then
        assertEquals(customException.getErrorCode(), ErrorCode.INVALID_DATE_RANGE);
    }

    @Test
    @DisplayName("크루 러닝 조회_날짜 검증 성공")
    void getCrewActivity_SuccessValidateDate() {
        // given
        Long crewId = 1L;
        ActivityFilterDto equalDate = ActivityFilterDto.builder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();
        MemberEntity memberEntity = MemberEntity.builder().nickName("nick").build();
        Pageable pageable = mock(Pageable.class);
        Page<ActivityEntity> activityList = new PageImpl<>(
            List.of(ActivityEntity.builder().author(memberEntity).build(),
                ActivityEntity.builder().author(memberEntity).build()));

        given(activityRepository.findByCrew_IdAndDateBetween(crewId,
            equalDate.getStartDate(), equalDate.getEndDate(), pageable)).willReturn(
            activityList);

        // when
        List<ActivityResponseDto> response = activityService.getCrewActivityByDate(crewId,
            equalDate, pageable);

        // then
        verify(activityRepository, times(1)).findByCrew_IdAndDateBetween(
            crewId, equalDate.getStartDate(), equalDate.getEndDate(), pageable);
        assertEquals(response.size(), activityList.getSize());
    }
}