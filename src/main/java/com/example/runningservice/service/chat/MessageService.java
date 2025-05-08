//package com.example.runningservice.service.chat;
//
//import com.example.runningservice.dto.chat.MessageEditRequestDto;
//import com.example.runningservice.dto.chat.MessageRequestDto;
//import com.example.runningservice.entity.chat.ChatJoinEntity;
//import com.example.runningservice.entity.chat.MessageEntity;
//import com.example.runningservice.enums.Message;
//import com.example.runningservice.repository.chat.ChatJoinRepository;
//import com.example.runningservice.repository.chat.MessageRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MessageService {
//
//    private final MessageRepository messageRepository;
//    private final ChatJoinRepository chatJoinRepository;
//
//    public MessageEntity saveMessage(MessageRequestDto messageRequestDto) throws Exception {
//        ChatJoinEntity chatJoinEntity = chatJoinRepository.findById(messageRequestDto.getChatJoinId())
//            .orElseThrow(() -> new RuntimeException("사용자가 채팅방에 참여중이지 않습니다."));
//
//        MessageEntity messageEntity = messageRequestDto.toEntity(chatJoinEntity);
//
//        return messageRepository.save(messageEntity);
//    }
//
//    public MessageEntity editMessage(Long messageId, MessageEditRequestDto MessageEditRequestDto) {
//        MessageEntity messageEntity = messageRepository.findById(messageId)
//            .orElseThrow(() -> new RuntimeException("해당 메시지를 찾을 수 없습니다."));
//
//        messageEntity.editContent(MessageEditRequestDto.getContent());
//
//        return messageRepository.save(messageEntity);
//    }
//
//    public void deleteMessage(Long messageId) {
//        MessageEntity messageEntity = messageRepository.findById(messageId)
//            .orElseThrow(() -> new RuntimeException("해당 메시지를 찾을 수 없습니다."));
//
//        messageRepository.delete(messageEntity);
//    }
//}
