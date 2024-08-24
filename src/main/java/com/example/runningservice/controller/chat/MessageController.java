package com.example.runningservice.controller.chat;

import com.example.runningservice.dto.chat.MessageRequestDto;
import com.example.runningservice.dto.chat.MessageResponseDto;
import com.example.runningservice.entity.chat.MessageEntity;
import com.example.runningservice.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/send/{room_id}")
    @SendTo("/topic/chatroom/{room_id}")
    public MessageResponseDto sendMessage(@DestinationVariable("room_id") Long roomId, MessageRequestDto messageRequestDto) throws Exception {

        MessageEntity messageEntity = messageService.saveMessage(messageRequestDto);

        return MessageResponseDto.of(messageEntity);
    }

    @GetMapping("/crew/{crew_id}/chatroom/{chatroom_id}/message")
    public void getChatRoomMessages(@PathVariable("crew_id") Long crewId,
                                    @PathVariable("chatroom_id") Long roomId) {

    }

}
