package com.example.runningservice.controller.chat;

import com.example.runningservice.dto.chat.ChatRoomRequestDto;
import com.example.runningservice.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
}
