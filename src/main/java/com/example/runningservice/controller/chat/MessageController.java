//package com.example.runningservice.controller.chat;
//
//import com.example.runningservice.dto.chat.MessageDeleteResponseDto;
//import com.example.runningservice.dto.chat.MessageEditRequestDto;
//import com.example.runningservice.dto.chat.MessageRequestDto;
//import com.example.runningservice.dto.chat.MessageResponseDto;
//import com.example.runningservice.entity.chat.MessageEntity;
//import com.example.runningservice.service.chat.MessageService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class MessageController {
//
//    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
//
//    private final MessageService messageService;
//    private final RabbitTemplate rabbitTemplate;
//
//    @MessageMapping("chat.talk.{room_id}")
//    public void sendMessage(@DestinationVariable("room_id") Long roomId,
//                                          @Payload MessageRequestDto messageRequestDto) throws Exception {
//
//        MessageEntity messageEntity = messageService.saveMessage(messageRequestDto);
//        if (messageEntity != null) {
//            MessageResponseDto messageResponseDto = MessageResponseDto.of(messageEntity);
//            rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chatroom." + roomId, messageResponseDto);
//            log.info("Message sent to RabbitMQ: {}", messageResponseDto);
//        } else {
//            log.error("Failed to create chat message User might not be in the chat room. ChatJoin: {}",
//                messageRequestDto.getChatJoinId());
//        }
//    }
//
//    // 메시지 수정
//    @MessageMapping("chat.edit.{room_id}.{message_id}")
//    public void editMessage(@DestinationVariable("room_id") Long roomId,
//                                          @DestinationVariable("message_id") Long messageId,
//                                          MessageEditRequestDto messageEditRequestDto) throws Exception {
//
//        MessageEntity messageEntity = messageService.editMessage(messageId, messageEditRequestDto);
//        if (messageEntity != null) {
//            MessageResponseDto messageResponseDto = MessageResponseDto.of(messageEntity);
//
//            rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chatroom." + roomId, messageResponseDto);
//            log.info("Updated message sent to RabbitMQ: {}", messageResponseDto);
//        } else {
//            log.error("Failed to update chat message. MessageId: {}", messageId);
//        }
//    }
//
//    // 메시지 삭제
//    @MessageMapping("chat.delete.{room_id}.{message_id}")
//    public void deleteMessage(@DestinationVariable("room_id") Long roomId,
//                                                  @DestinationVariable("message_id") Long messageId) {
//        messageService.deleteMessage(messageId);
//        MessageDeleteResponseDto messageDeleteResponseDto = new MessageDeleteResponseDto(messageId, "메시지가 삭제되었습니다.");
//
//        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chatroom." + roomId, messageDeleteResponseDto);
//        log.info("Deleted message sent to RabbitMQ: {}", messageDeleteResponseDto);
//    }
////
////    // 메시지 조회
////    @GetMapping("/crew/{crew_id}/chatroom/{chatroom_id}/message")
////    public void getChatRoomMessages(@PathVariable("crew_id") Long crewId,
////                                    @PathVariable("chatroom_id") Long roomId) {
////
////    }
//
//    // 답장 기능
//
//    // 멘션 기능
//
//
//
//}
