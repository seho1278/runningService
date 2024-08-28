package com.example.runningservice.controller;

import com.example.runningservice.dto.NotificationResponseDto;
import com.example.runningservice.service.notification.NotificationService;
import com.example.runningservice.util.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 크루 일정 생성 알림 topic을 구독한다. (크루)
     */
    @MessageMapping("/sub/activity/{crewId}")
    @SendTo("/topic/activity/{crewId}")
    public void subscribeActivityNotification(@LoginUser Long loginId,
        @DestinationVariable("crewId") Long crewId) {
        notificationService.subscribeActivityNotification(loginId);
    }

    /**
     * 채팅 메시지 알림 topic을 구독한다. (채팅방)
     */
    @MessageMapping("/sub/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public void subscribeChatNotification(@LoginUser Long loginId,
        @DestinationVariable("roomId") Long roomId) {
        notificationService.subscribeChatNotification(loginId);
    }

    /**
     * 사용자 알림 topic을 구독한다. (멘션, 가입 신청 결과)
     */
    @MessageMapping("/sub/user/{userId}")
    @SendTo("/topic/user/{userId}")
    public void subscribeUserNotification(@DestinationVariable("userId") Long userId) {

    }

    /**
     * 사용자에게 온 모든 알림을 조회한다.
     */
    @GetMapping("/notification")
    public ResponseEntity<List<NotificationResponseDto>> getNotification(@LoginUser Long loginId,
        Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotification(loginId, pageable));
    }

    /**
     * 사용자가 알림을 읽어서 읽은 표시를 남긴다.
     */
    @PutMapping("/notification/{notificationId}")
    public ResponseEntity<NotificationResponseDto> readNotification(
        @PathVariable("notificationId") Long notificationId) {
        return ResponseEntity.ok(notificationService.readNotification(notificationId));
    }
}
