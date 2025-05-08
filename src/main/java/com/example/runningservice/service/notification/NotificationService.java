package com.example.runningservice.service.notification;

import com.example.runningservice.dto.NotificationRequestDto;
import com.example.runningservice.dto.NotificationResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.NotificationEntity;
import com.example.runningservice.entity.UserNotificationEntity;
import com.example.runningservice.enums.Notification;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.NotificationRepository;
import com.example.runningservice.repository.UserNotificationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;

    /**
     * 사용자에게 온 모든 알림을 조회한다.
     */
    public List<NotificationResponseDto> getNotification(Long userId, Pageable pageable) {
        Page<UserNotificationEntity> notificationList = userNotificationRepository
            .findByMember_IdOrderByNotification_CreatedAtDesc(userId, pageable);

        List<NotificationResponseDto> response = new ArrayList<>();
        for (UserNotificationEntity notificationEntity : notificationList) {
            response.add(NotificationResponseDto.of(notificationEntity));
        }

        return response;
    }

    /**
     * 사용자들에게 알림을 전송한다.
     */
    public void sendNotification(NotificationManagerService notificationManager,
        NotificationRequestDto request) {
        // 전송할 메시지 설정
        request.setNotiMessage(
            notificationManager.getMessage(request.getRelatedId(), request.getRelatedType()));

        // 알림 DB 저장
        NotificationEntity notificationEntity = NotificationEntity.toEntity(request);
        notificationRepository.save(notificationEntity);

        // 메시지 전송
        messagingTemplate.convertAndSend(request.getTopic(), request.getMessage());

        // 사용자별 알림 저장
        List<MemberEntity> subscriberList = notificationManager.findSubscriber(
            request.getRelatedId(), request.getRelatedType());

        userNotificationRepository.saveAll(subscriberList.stream().map(member ->
            UserNotificationEntity.builder()
                .notification(notificationEntity)
                .member(member)
                .build()
        ).toList());
    }

    /**
     * 사용자가 알림을 읽어서 읽은 표시를 남긴다.
     */
    @Transactional
    public NotificationResponseDto readNotification(Long userNotiId) {
        UserNotificationEntity userNotiEntity = userNotificationRepository.findById(userNotiId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_NOTIFICATION));

        userNotiEntity.read();

        return NotificationResponseDto.of(userNotiEntity);
    }

    /**
     * 크루 일정 생성 알림을 꺼둔 사용자는 토픽 구독을 거부한다.
     */
    public void subscribeActivityNotification(Long loginId) {
        MemberEntity memberEntity = memberRepository.findMemberById(loginId);

        if (memberEntity.getActivityNoti() == Notification.OFF) {
            throw new CustomException(ErrorCode.REJECT_SUBSCRIBE_NOTIFICATION);
        }
    }

    /**
     * 채팅 메시지 알림을 꺼둔 사용자는 토픽 구독을 거부한다.
     */
    public void subscribeChatNotification(Long loginId) {
        MemberEntity memberEntity = memberRepository.findMemberById(loginId);

        if (memberEntity.getChattingNoti() == Notification.OFF) {
            throw new CustomException(ErrorCode.REJECT_SUBSCRIBE_NOTIFICATION);
        }
    }
}
