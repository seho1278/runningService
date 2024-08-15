package com.example.runningservice.controller.chat;

import com.example.runningservice.dto.chat.ChatRoomRequestDto;
import com.example.runningservice.service.chat.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crew/{crew_id}")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

}
