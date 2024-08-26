package com.example.runningservice.service;

import com.example.runningservice.dto.activity.ParticipantResponseDto;
import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.ParticipantEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.ParticipantRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ActivityRepository activityRepository;

    /**
     * 러닝에 참석 신청한다.
     */
    @Transactional
    public ParticipantResponseDto participateActivity(Long userId, Long activityId) {
        MemberEntity memberEntity = memberRepository.findMemberById(userId);

        ActivityEntity activityEntity = activityRepository.findById(activityId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        ParticipantEntity participant = ParticipantEntity.builder()
            .member(memberEntity)
            .activity(activityEntity)
            .build();

        participantRepository.save(participant);

        return ParticipantResponseDto.builder()
            .userId(userId)
            .activityId(activityId)
            .nickName(memberEntity.getNickName())
            .birthYear(memberEntity.getBirthYear())
            .gender(memberEntity.getGender())
            .build();
    }

    /**
     * 러닝 참석을 취소한다.
     */
    @Transactional
    public ParticipantResponseDto cancelParticipateActivity(Long userId, Long activityId) {
        MemberEntity memberEntity = memberRepository.findMemberById(userId);

        activityRepository.findById(activityId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        participantRepository.deleteByMember_IdAndActivity_Id(userId, activityId);

        return ParticipantResponseDto.builder()
            .userId(userId)
            .activityId(activityId)
            .nickName(memberEntity.getNickName())
            .birthYear(memberEntity.getBirthYear())
            .gender(memberEntity.getGender())
            .build();
    }

    /**
     * 활동 참석자를 조회한다.
     */
    @Transactional
    public List<ParticipantResponseDto> getActivityParticipant(Long activityId, Pageable pageable) {
        Page<ParticipantEntity> participantPage = participantRepository.findByActivity_Id(
            activityId, pageable);

        List<ParticipantResponseDto> response = new ArrayList<>();
        for (ParticipantEntity participant : participantPage) {
            response.add(ParticipantResponseDto.builder()
                .userId(participant.getMember().getId())
                .activityId(activityId)
                .nickName(participant.getMember().getNickName())
                .birthYear(participant.getMember().getBirthYear())
                .gender(participant.getMember().getGender())
                .build());
        }

        return response;
    }
}
