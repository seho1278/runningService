package com.example.runningservice.dto.chat;

import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.MessageEntity;
import com.example.runningservice.enums.Message;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDto {
    private Message type;
    private Long chatJoinId;
    private String content;
    private String imageUrl;

    public MessageEntity toEntity(ChatJoinEntity chatJoin) throws Exception {
        return MessageEntity.builder()
            .messageType(type)
            .content(content)
            .imageUrl(imageUrl)
            .chatJoin(chatJoin)
            .build();
    }

}
