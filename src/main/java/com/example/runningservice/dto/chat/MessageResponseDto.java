package com.example.runningservice.dto.chat;

import com.example.runningservice.entity.chat.MessageEntity;
import com.example.runningservice.enums.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {
    private Message type;
    private Long roomId;
    private String sender;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static MessageResponseDto of (MessageEntity messageEntity) throws Exception {
        return MessageResponseDto.builder()
            .roomId(messageEntity.getRoomId())
            .type(messageEntity.getMessageType())
            .content(messageEntity.getContent())
            .imageUrl(messageEntity.getImageUrl())
            .sender(messageEntity.getSender())
            .createdAt(messageEntity.getCreatedAt())
            .build();
    }

}
