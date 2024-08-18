package com.example.runningservice.dto.chat;

import com.example.runningservice.enums.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponseDto {

    private Long id;
    private String roomName;

}
