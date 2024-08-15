package com.example.runningservice.dto.chat;

import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.enums.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequestDto {

    private String roomName;
    private ChatRoom roomType;

    public ChatRoomEntity toEntity(String roomName, ChatRoom roomType) throws Exception {
        return ChatRoomEntity.builder()
            .roomName(roomName)
            .roomType(roomType)
            .build();
    }

//    private Set<WebSocketSession> sessions = new HashSet<>();
//
//    public ChatRoomRequestDto(Long crewId, String roomName, ChatRoom roomType) {
//        this.crewId = crewId;
//        this.roomName = roomName;
//        this.roomType = roomType;
//    }

//    public void handleAction(WebSocketSession session, Message message, ChatRoomService chatRoomService) {
//        if (message.getType().equals(message))
//    }
}
