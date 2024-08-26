package com.example.runningservice.entity;

import com.example.runningservice.dto.NotificationRequestDto;
import com.example.runningservice.enums.NotificationType;
import com.example.runningservice.enums.TableType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String message;
    @CreatedDate
    private LocalDateTime createdAt;
    private Long relatedId;
    @Enumerated(EnumType.STRING)
    private TableType relatedType;

    public static NotificationEntity toEntity(NotificationRequestDto notificationDto) {
        return NotificationEntity.builder()
            .type(notificationDto.getNotificationType())
            .message(notificationDto.getMessage())
            .relatedType(notificationDto.getRelatedType())
            .relatedId(notificationDto.getRelatedId())
            .build();
    }
}
