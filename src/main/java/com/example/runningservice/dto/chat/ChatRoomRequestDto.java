package com.example.runningservice.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomRequestDto {
    private String roomName;
    private String roomType;
}
