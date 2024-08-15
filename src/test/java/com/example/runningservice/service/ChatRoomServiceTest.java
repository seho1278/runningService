package com.example.runningservice.service;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.example.runningservice.service.chat.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;

    @Mock
    CrewRepository crewRepository;

    @InjectMocks
    ChatRoomService chatRoomService;

    @Test
    public void createChatRoom_success() {
        // given
        Long crewId = 1L;
        String crewName = "runningcrew";
        ChatRoom chatRoom = ChatRoom.CREW;

        CrewEntity mockCrewEntity = CrewEntity.builder()
            .crewId(crewId)
            .crewName(crewName)
            .build();

        ChatRoomEntity mockChatRoomEntity = ChatRoomEntity.builder()
            .roomName(mockCrewEntity.getCrewName())
            .roomType(chatRoom)
            .crewEntity(mockCrewEntity)
            .build();

        when(crewRepository.findById(crewId)).thenReturn(Optional.of(mockCrewEntity));
        when(chatRoomRepository.save(any(ChatRoomEntity.class))).thenReturn(mockChatRoomEntity);

        // when
        chatRoomService.createChatRoom(mockCrewEntity.getCrewId(), mockCrewEntity.getCrewName(), chatRoom);

        // then
        ArgumentCaptor<ChatRoomEntity> chatRoomEntityCaptor = ArgumentCaptor.forClass(ChatRoomEntity.class);
        verify(chatRoomRepository, times(1)).save(chatRoomEntityCaptor.capture());

        ChatRoomEntity capturedChatRoomEntity = chatRoomEntityCaptor.getValue();

        assertEquals(crewName, capturedChatRoomEntity.getRoomName());
        assertEquals(chatRoom, capturedChatRoomEntity.getRoomType());
        assertEquals(mockCrewEntity, capturedChatRoomEntity.getCrewEntity());
    }
}
