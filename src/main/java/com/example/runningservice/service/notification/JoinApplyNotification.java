package com.example.runningservice.service.notification;

import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.TableType;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.JoinApplicationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinApplyNotification implements NotificationManagerService {

    private final JoinApplicationRepository joinApplicationRepository;

    @Override
    public String getMessage(Long relatedId, TableType relatedType) {
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findById(relatedId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        return  "크루 가입 신청 결과가 나왔습니다. "
            + joinApplyEntity.getCrew().getCrewName()
            + " :: "
            + joinApplyEntity.getStatus();
    }

    @Override
    public List<MemberEntity> findSubscriber(Long relatedId, TableType relatedType) {
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findById(relatedId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        return List.of(joinApplyEntity.getMember());
    }
}
