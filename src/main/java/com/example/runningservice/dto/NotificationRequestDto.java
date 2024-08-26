package com.example.runningservice.dto;

import com.example.runningservice.enums.NotificationType;
import com.example.runningservice.enums.TableType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto {

    private NotificationType notificationType;
    private String message;
    private String topic;
    private Long relatedId;
    private TableType relatedType;

    public void setNotiMessage(String message) {
        this.message = message;
    }
}
