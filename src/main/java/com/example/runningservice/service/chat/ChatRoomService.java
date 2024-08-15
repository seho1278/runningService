package com.example.runningservice.service.chat;

import com.example.runningservice.dto.chat.ChatRoomRequestDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final CrewRepository crewRepository;

    public void createChatRoom(Long crewId, String roomName, ChatRoom roomType) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .roomName(roomName)
            .roomType(roomType)
            .crew(crewEntity)
            .build();

        chatRoomRepository.save(chatRoomEntity);
    }

}
