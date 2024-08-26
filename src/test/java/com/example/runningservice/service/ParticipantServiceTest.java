package com.example.runningservice.service;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.runningservice.dto.activity.ParticipantResponseDto;
import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.ParticipantEntity;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.ParticipantRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ActivityRepository activityRepository;
    @InjectMocks
    private ParticipantService participantService;

    @Test
    @DisplayName("활동 참석 신청")
    void participateActivity() {
        // given
        Long userId = 1L;
        Long activityId = 2L;

        MemberEntity memberEntity = MemberEntity.builder().id(userId).nickName("nick").build();
        ActivityEntity activityEntity = ActivityEntity.builder().id(activityId).build();
        given(memberRepository.findMemberById(userId)).willReturn(memberEntity);
        given(activityRepository.findById(activityId)).willReturn(Optional.of(activityEntity));

        // when
        ParticipantResponseDto response = participantService.participateActivity(userId,
            activityId);

        // then
        Assertions.assertEquals(response.getActivityId(), activityId);
        Assertions.assertEquals(response.getUserId(), userId);
    }

    @Test
    @DisplayName("활동 참석 취소")
    void cancelParticipateActivity() {
        // given
        Long userId = 1L;
        Long activityId = 2L;

        MemberEntity memberEntity = MemberEntity.builder().id(userId).nickName("nick").build();
        ActivityEntity activityEntity = ActivityEntity.builder().id(activityId).build();
        given(memberRepository.findMemberById(userId)).willReturn(memberEntity);
        given(activityRepository.findById(activityId)).willReturn(Optional.of(activityEntity));

        // when
        ParticipantResponseDto response = participantService.cancelParticipateActivity(userId,
            activityId);

        // then
        verify(participantRepository, times(1))
            .deleteByMember_IdAndActivity_Id(userId, activityId);
        Assertions.assertEquals(response.getActivityId(), activityId);
        Assertions.assertEquals(response.getUserId(), userId);
    }

    @Test
    @DisplayName("활동 참석자를 조회")
    void getActivityParticipant() {
        // given
        Long activityId = 2L;
        Pageable pageable = mock(Pageable.class);
        MemberEntity member1 = MemberEntity.builder().nickName("nick1").build();
        MemberEntity member2 = MemberEntity.builder().nickName("nick2").build();

        List<ParticipantEntity> participantList = List.of(
            ParticipantEntity.builder().member(member1).build(),
            ParticipantEntity.builder().member(member2).build());
        Page<ParticipantEntity> participantPage = new PageImpl<>(participantList);

        given(participantRepository.findByActivity_Id(activityId, pageable)).willReturn(
            participantPage);

        // when
        List<ParticipantResponseDto> response = participantService.getActivityParticipant(
            activityId, pageable);

        // then
        Assertions.assertEquals(response.size(), participantList.size());
        Assertions.assertEquals(response.get(0).getNickName(), member1.getNickName());
        Assertions.assertEquals(response.get(1).getNickName(), member2.getNickName());
    }
}