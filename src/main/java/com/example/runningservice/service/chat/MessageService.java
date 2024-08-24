package com.example.runningservice.service.chat;

import com.example.runningservice.dto.chat.MessageRequestDto;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.MessageEntity;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatJoinRepository chatJoinRepository;

    public MessageEntity saveMessage(MessageRequestDto messageRequestDto) throws Exception {
        ChatJoinEntity chatJoinEntity = chatJoinRepository.findById(messageRequestDto.getChatJoinId())
            .orElseThrow(() -> new RuntimeException("ChatJoin을 찾을 수 없습니다."));

        MessageEntity messageEntity = messageRequestDto.toEntity(chatJoinEntity);

        return messageRepository.save(messageEntity);
    }
}
