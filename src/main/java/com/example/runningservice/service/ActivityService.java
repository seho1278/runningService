package com.example.runningservice.service;

import com.example.runningservice.dto.activity.ActivityFilterDto;
import com.example.runningservice.dto.activity.ActivityRequestDto.Create;
import com.example.runningservice.dto.activity.ActivityRequestDto.Update;
import com.example.runningservice.dto.activity.ActivityResponseDto;
import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.ActivityCategory;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    // private final RegularRunMeetingRepository regularRunMeetingRepository;
    // private final NotificationService notificationService;
    // private final ActivityNotification activityNotification;

    // 정기 러닝 일정 생성
    @Transactional
    public ActivityResponseDto createRegularActivity(Long userId, Long crewId, Create activity) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        MemberEntity authorEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (hasNotLeaderOfStaffAuthority(crewId, userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REGULAR_ACCESS);
        }

        // 정기러닝 id 등록 방식은 현재 기능에서 제공하지 않음
        /*RegularRunMeetingEntity regularEntity = regularRunMeetingRepository.findById(
                activity.getRegularId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGULAR_RUN));*/

        ActivityEntity activityEntity = ActivityEntity.toEntity(
            activity, authorEntity, crewEntity, null);

        activityRepository.save(activityEntity);

        // 크루원들에게 일정 추가 알림 전송 - 현재 사용 안함
        /*notificationService.sendNotification(activityNotification,
            NotificationRequestDto.builder()
                .topic("/topic/activity/" + crewId)
                .notificationType(NotificationType.ACTIVITY)
                .relatedType(TableType.ACTIVITY)
                .relatedId(activityEntity.getId())
                .build());*/

        return ActivityResponseDto.fromEntity(activityEntity, userId);
    }

    // 번개 러닝 일정 생성
    @Transactional
    public ActivityResponseDto createOnDemandActivity(Long userId, Long crewId, Create activity) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        MemberEntity authorEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        ActivityEntity activityEntity = ActivityEntity.toEntity(
            activity, authorEntity, crewEntity, null);

        activityRepository.save(activityEntity);

        // 크루원들에게 일정 추가 알림 전송 - 현재 사용 안함
        /*notificationService.sendNotification(activityNotification,
            NotificationRequestDto.builder()
                .topic("/topic/activity/" + crewId)
                .notificationType(NotificationType.ACTIVITY)
                .relatedType(TableType.ACTIVITY)
                .relatedId(activityEntity.getId())
                .build());*/

        return ActivityResponseDto.fromEntity(activityEntity, userId);
    }

    // 리더 또는 스탭 권한이 없는지 확인한다.
    private boolean hasNotLeaderOfStaffAuthority(Long crewId, Long userId) {
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByCrew_IdAndMember_Id(
                crewId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_CREW_ACCESS));

        return !crewMemberEntity.getRole().equals(CrewRole.LEADER) &&
            !crewMemberEntity.getRole().equals(CrewRole.STAFF);
    }

    /**
     * (정기/번개)러닝 일정 수정
     */
    @Transactional
    public ActivityResponseDto updateActivity(Long userId, Long crewId, Long activityId,
        Update activityDto) {
        ActivityEntity activityEntity = activityRepository.findById(activityId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        if (isRegularRun(activityEntity) && hasNotLeaderOfStaffAuthority(crewId, userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REGULAR_ACCESS);
        }
        if (!isRegularRun(activityEntity) && !activityEntity.getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTIVITY);
        }

        activityEntity.update(activityDto);

        return ActivityResponseDto.fromEntity(activityEntity, userId);
    }

    private boolean isRegularRun(ActivityEntity activityEntity) {
        return activityEntity.getCategory().equals(ActivityCategory.REGULAR);
    }

    /**
     * (정기/번개)러닝 일정 삭제
     */
    @Transactional
    public ActivityResponseDto deleteActivity(Long userId, Long crewId, Long activityId) {
        ActivityEntity activityEntity = activityRepository.findById(activityId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        if (hasNotLeaderOfStaffAuthority(crewId, userId)) { // 리더나 스탭이 아니면 본인 일정만 삭제 가능
            if (!activityEntity.getAuthor().getId().equals(userId)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACTIVITY);
            }
        }

        ActivityResponseDto response = ActivityResponseDto.fromEntity(activityEntity, userId);

        activityRepository.delete(activityEntity);

        return response;
    }

    /**
     * 특정 (정기/번개)러닝 일정 조회
     */
    public ActivityResponseDto getActivity(Long activityId, Long userId) {
        ActivityEntity activityEntity = activityRepository.findById(activityId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        return ActivityResponseDto.fromEntity(activityEntity, userId);
    }

    /**
     * 날짜별 크루 (정기/번개)러닝 일정 조회
     */
    public Page<ActivityResponseDto> getCrewActivityByDate(Long crewId, Long userId,
        ActivityFilterDto activityFilter, Pageable pageable) {
        if (!validateDate(activityFilter.getStartDate(), activityFilter.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }

        Page<ActivityEntity> activityPage = activityRepository.findByCrewIdAndCategoryAndDateBetween(
            crewId, activityFilter.getCategory(), activityFilter.getStartDate(),
            activityFilter.getEndDate(), pageable);

        return activityPage.map(entity -> ActivityResponseDto.fromEntity(entity, userId));
    }

    /**
     * 다가오는 크루 (정기/번개)러닝 일정 조회
     */
    public Page<ActivityResponseDto> getCrewActivity(Long crewId, Long userId,
        ActivityCategory category, Pageable pageable) {
        Page<ActivityEntity> activityPage = activityRepository
            .findByCrew_IdAndCategoryAndDateGreaterThanEqualOrderByDate(
                crewId, category, pageable);

        return activityPage.map(entity -> ActivityResponseDto.fromEntity(entity, userId));
    }

    // 시작 날짜가 종료 날짜보다 빠르거나 같은지 체크한다.
    private boolean validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return startDate.isBefore(endDate) || startDate.isEqual(endDate);
        }
        return true;
    }
}
