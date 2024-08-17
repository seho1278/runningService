package com.example.runningservice.service;

import com.example.runningservice.dto.activity.ActivityRequestDto.Create;
import com.example.runningservice.dto.activity.ActivityResponseDto;
import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RegularRunMeetingEntity;
import com.example.runningservice.enums.ActivityCategory;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final RegularRunMeetingRepository regularRunMeetingRepository;

    /**
     * (정기/번개)러닝 일정 생성
     */
    public ActivityResponseDto createActivity(Long userId, Long crewId, Create newActivity) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        MemberEntity authorEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 정기러닝 생성 시 일정에 정기러닝 정보를 설정하고, 번개러닝 생성 시 null로 설정
        RegularRunMeetingEntity regularEntity =
            (newActivity.getCategory().equals(ActivityCategory.REGULAR)) ?
                getRegularActivity(newActivity, crewId, userId) : null;

        ActivityEntity activityEntity = ActivityEntity.builder()
            .author(authorEntity)
            .crew(crewEntity)
            .regularRun(regularEntity)
            .title(newActivity.getTitle())
            .location(newActivity.getLocation())
            .date(newActivity.getDate())
            .notes(newActivity.getMemo())
            .startTime(newActivity.getStartTime())
            .endTime(newActivity.getEndTime())
            .build();

        activityRepository.save(activityEntity);

        return ActivityResponseDto.fromEntity(activityEntity);
    }

    // 정기러닝 생성 권한 체크 후, 정기러닝 정보 엔티티 가져오기
    private RegularRunMeetingEntity getRegularActivity(Create newActivity, Long crewId,
        Long userId) {

        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByCrew_CrewIdAndMember_Id(
                crewId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_CREW_ACCESS));

        // 정기러닝 생성은 LEADER 또는 STAFF 권한만 가능
        if (crewMemberEntity.getRole().equals(CrewRole.MEMBER)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_CREW_ACCESS);
        }

        return regularRunMeetingRepository.findById(newActivity.getRegularId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGULAR_RUN));
    }
}
