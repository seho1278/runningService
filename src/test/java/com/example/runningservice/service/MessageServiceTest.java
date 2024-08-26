//package com.example.runningservice.service;
//
//import com.example.runningservice.dto.chat.MessageRequestDto;
//import com.example.runningservice.entity.MemberEntity;
//import com.example.runningservice.entity.chat.ChatJoinEntity;
//import com.example.runningservice.entity.chat.ChatRoomEntity;
//import com.example.runningservice.entity.chat.MessageEntity;
//import com.example.runningservice.enums.Message;
//import com.example.runningservice.repository.chat.ChatJoinRepository;
//import com.example.runningservice.repository.chat.MessageRepository;
//import com.example.runningservice.service.chat.MessageService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class MessageServiceTest {
//
//    @InjectMocks
//    private MessageService messageService;
//
//    @Mock
//    private MessageRepository messageRepository;
//
//    @Mock
//    private ChatJoinRepository chatJoinRepository;
//
//    @Test
//    public void testSaveMessage() throws Exception {
//        // given
//        Long chatJoinId = 1L;
//        Long roomId = 1L;
//        Long memberId = 1L;
//
//        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
//            .type(Message.TALK)  // 예시로 Message.TEXT 사용
//            .chatJoinId(chatJoinId)
//            .content("Test message")
//            .imageUrl("http://example.com/image.jpg")
//            .build();
//
//        ChatJoinEntity chatJoinEntity = ChatJoinEntity.builder()
//            .id(chatJoinId)
//            .chatRoom(ChatRoomEntity.builder().id(roomId).build())
//            .member(MemberEntity.builder().id(memberId).build())
//            .build();
//
//        when(chatJoinRepository.findById(chatJoinId)).thenReturn(Optional.of(chatJoinEntity));
//
//        MessageEntity messageEntity = MessageEntity.builder()
//            .messageType(messageRequestDto.getType())
//            .content(messageRequestDto.getContent())
//            .imageUrl(messageRequestDto.getImageUrl())
//            .chatJoin(chatJoinEntity)
//            .build();
//
//        when(messageRepository.save(any(MessageEntity.class))).thenReturn(messageEntity);
//
//        // when
//        MessageEntity result = messageService.saveMessage(messageRequestDto);
//
//        // Then
//        assertEquals("Test message", result.getContent());
//        assertEquals("http://example.com/image.jpg", result.getImageUrl());
//        assertEquals(Message.TALK, result.getMessageType());
//        verify(chatJoinRepository).findById(chatJoinId);
//        verify(messageRepository).save(any(MessageEntity.class));
//
//    }
//
//}
