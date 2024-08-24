package com.example.runningservice.service.notification;

import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.TableType;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.CrewMemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityNotification implements NotificationManagerService {

    private final ActivityRepository activityRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Override
    public String getMessage(Long relatedId, TableType relatedType) {
        ActivityEntity activityEntity = activityRepository.findById(relatedId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        return "새 일정이 등록되었습니다. "
            + activityEntity.getCrew().getCrewName()
            + " :: "
            + activityEntity.getDate()
            + " "
            + activityEntity.getStartTime();
    }

    @Override
    public List<MemberEntity> findSubscriber(Long relatedId, TableType relatedType) {
        ActivityEntity activityEntity = activityRepository.findById(relatedId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACTIVITY));

        List<CrewMemberEntity> crewMemberList = crewMemberRepository.findByCrew(
            activityEntity.getCrew());

        return crewMemberList.stream().map(CrewMemberEntity::getMember)
            .collect(Collectors.toList());
    }
}
