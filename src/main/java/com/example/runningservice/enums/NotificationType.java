package com.example.runningservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    ACTIVITY, POST, MENTION, CHATTING, REPLY, APPLY_RESULT
}
