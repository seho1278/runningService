package com.example.runningservice.dto;

import com.example.runningservice.entity.UserNotificationEntity;
import com.example.runningservice.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {

    private Long notificationId;
    private NotificationType type;
    private Long relatedId;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;

    public static NotificationResponseDto of(UserNotificationEntity notificationEntity) {
        return NotificationResponseDto.builder()
            .notificationId(notificationEntity.getId())
            .type(notificationEntity.getNotification().getType())
            .relatedId(notificationEntity.getNotification().getRelatedId())
            .message(notificationEntity.getNotification().getMessage())
            .createdAt(notificationEntity.getNotification().getCreatedAt())
            .readAt(notificationEntity.getReadAt())
            .build();
    }
}
