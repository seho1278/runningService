package com.example.runningservice.service.notification;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.MessageEntity;
import com.example.runningservice.enums.TableType;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.chat.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatNotification implements NotificationManagerService {

    private final MessageRepository messageRepository;
    private final ChatJoinRepository chatJoinRepository;

    @Override
    public String getMessage(Long relatedId, TableType relatedType) {
        MessageEntity messageEntity = messageRepository.findById(relatedId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHAT_MESSAGE));

        return messageEntity.getSender()
            + " : "
            + messageEntity.getContent();
    }

    @Override
    public List<MemberEntity> findSubscriber(Long relatedId, TableType relatedType) {
        MessageEntity messageEntity = messageRepository.findById(relatedId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHAT_MESSAGE));

        List<ChatJoinEntity> chatJoinList = chatJoinRepository.findByChatRoom_Id(
            messageEntity.getChatJoin().getChatRoom().getId());

        return chatJoinList.stream().map(ChatJoinEntity::getMember).toList();
    }
}
