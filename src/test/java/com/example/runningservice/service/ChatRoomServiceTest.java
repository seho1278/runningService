package com.example.runningservice.service;

import com.example.runningservice.dto.chat.ChatRoomDetailsDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.example.runningservice.repository.chat.MessageRepository;
import com.example.runningservice.service.chat.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private ChatJoinRepository chatJoinRepository;

    @Mock
    private MessageRepository messageRepository;

    private CrewEntity crewEntity;
    private MemberEntity memberAEntity;
    private MemberEntity memberBEntity;
    private CrewMemberEntity crewMemberAEntity;
    private CrewMemberEntity crewMemberBEntity;

    @BeforeEach
    public void setUp() {
        crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("runningCrew")
            .build();

        memberAEntity = MemberEntity.builder()
            .id(1L)
            .nickName("potter")
            .build();

        memberBEntity = MemberEntity.builder()
            .id(2L)
            .nickName("harry")
            .build();

        crewMemberAEntity = CrewMemberEntity.builder()
            .id(1L)
            .crew(crewEntity)
            .member(memberAEntity)
            .role(CrewRole.LEADER)
            .build();

        crewMemberBEntity = CrewMemberEntity.builder()
            .id(2L)
            .crew(crewEntity)
            .member(memberBEntity)
            .role(CrewRole.MEMBER)
            .build();
    }

    @Test
    public void createChatRoom_success() {
        // Given
        when(crewRepository.findCrewById(crewEntity.getCrewId())).thenReturn(crewEntity);

        ChatRoomEntity mockChatRoomEntity = ChatRoomEntity.builder()
            .roomName(crewEntity.getCrewName())
            .roomType(ChatRoom.CREW)
            .crew(crewEntity)
            .build();

        when(chatRoomRepository.save(any(ChatRoomEntity.class))).thenReturn(mockChatRoomEntity);

        // When
        chatRoomService.createChatRoom(crewEntity.getCrewId(), crewEntity.getCrewName(), ChatRoom.CREW);

        // Then
        ArgumentCaptor<ChatRoomEntity> chatRoomEntityCaptor = ArgumentCaptor.forClass(ChatRoomEntity.class);
        verify(chatRoomRepository, times(1)).save(chatRoomEntityCaptor.capture());

        ChatRoomEntity capturedChatRoomEntity = chatRoomEntityCaptor.getValue();

        assertEquals(crewEntity.getCrewName(), capturedChatRoomEntity.getRoomName());
        assertEquals(ChatRoom.CREW, capturedChatRoomEntity.getRoomType());
        assertEquals(crewEntity, capturedChatRoomEntity.getCrew());
    }

    @Test
    public void testGetCrewChatRoomListForMember_success() {
        Long crewId = 1L;
        Long memberAId = 1L;
        Long memberBId = 2L;

        ChatRoomEntity chatRoom1 = ChatRoomEntity.builder()
            .id(1L)
            .roomName(crewEntity.getCrewName())
            .roomType(ChatRoom.CREW)
            .crew(crewEntity)
            .build();

        ChatRoomEntity chatRoom2 = ChatRoomEntity.builder()
            .id(2L)
            .roomName("Personal Chat")
            .roomType(ChatRoom.PERSONAL)
            .crew(crewEntity)
            .build();

        ChatJoinEntity chatJoin1 = ChatJoinEntity.builder()
            .id(1L)
            .chatRoom(chatRoom1)
            .member(memberAEntity)
            .joinedAt(LocalDateTime.now().minusDays(1))
            .readAt(LocalDateTime.now().minusHours(2))
            .build();

        ChatJoinEntity chatJoin2 = ChatJoinEntity.builder()
            .id(2L)
            .chatRoom(chatRoom2)
            .member(memberAEntity)
            .joinedAt(LocalDateTime.now().minusDays(1))
            .readAt(LocalDateTime.now().minusHours(2))
            .build();

        ChatJoinEntity chatJoin3 = ChatJoinEntity.builder()
            .id(3L)
            .chatRoom(chatRoom1)
            .member(memberBEntity)
            .joinedAt(LocalDateTime.now().minusDays(1))
            .readAt(LocalDateTime.now().minusHours(2))
            .build();

        ChatJoinEntity chatJoin4 = ChatJoinEntity.builder()
            .id(4L)
            .chatRoom(chatRoom1)
            .member(memberBEntity)
            .joinedAt(LocalDateTime.now().minusDays(1))
            .readAt(LocalDateTime.now().minusHours(2))
            .build();

        when(crewRepository.findCrewById(crewId)).thenReturn(crewEntity);
        when(memberRepository.findMemberById(memberAId)).thenReturn(memberAEntity);
        when(chatJoinRepository.findByMemberAndChatRoom_Crew(memberAEntity, crewEntity))
            .thenReturn(Arrays.asList(chatJoin1, chatJoin2));
        when(chatJoinRepository.findMemberNicknamesByChatRoom(chatRoom1))
            .thenReturn(Arrays.asList(memberAEntity, memberBEntity));
        when(chatJoinRepository.findMemberNicknamesByChatRoom(chatRoom2))
            .thenReturn(Arrays.asList(memberAEntity, memberBEntity));
        when(messageRepository.countMessagesAfterReadAt(eq(chatJoin1), any(LocalDateTime.class))).thenReturn(5);
        when(messageRepository.countMessagesAfterReadAt(eq(chatJoin2), any(LocalDateTime.class))).thenReturn(2);
        when(chatJoinRepository.countByChatRoom(chatRoom1)).thenReturn(2);
        when(chatJoinRepository.countByChatRoom(chatRoom2)).thenReturn(2);

        // when
        Map<ChatRoomEntity, ChatRoomDetailsDto> result = chatRoomService.getCrewChatRoomListForMember(crewId, memberAId);

        // then
        verify(crewRepository).findCrewById(crewId);
        verify(memberRepository).findMemberById(memberAId);
        verify(chatJoinRepository).findByMemberAndChatRoom_Crew(memberAEntity, crewEntity);
        verify(chatJoinRepository).findMemberNicknamesByChatRoom(chatRoom1);
        verify(chatJoinRepository).findMemberNicknamesByChatRoom(chatRoom2);
        verify(messageRepository).countMessagesAfterReadAt(chatJoin1, chatJoin1.getReadAt());
        verify(messageRepository).countMessagesAfterReadAt(chatJoin2, chatJoin2.getReadAt());
        verify(chatJoinRepository).countByChatRoom(chatRoom1);
        verify(chatJoinRepository).countByChatRoom(chatRoom2);

        assertNotNull(result);
        assertEquals(2, result.size());
        ChatRoomDetailsDto chatRoom1Details = result.get(chatRoom1);
        assertNotNull(chatRoom1Details);
        assertEquals(5, chatRoom1Details.getMessageCount());
        assertEquals(2, chatRoom1Details.getMemberCount());
        assertTrue(chatRoom1Details.getMemberEntityList().containsAll(Arrays.asList(memberAEntity, memberBEntity)));

        ChatRoomDetailsDto chatRoom2Details = result.get(chatRoom2);
        assertNotNull(chatRoom2Details);
        assertEquals(2, chatRoom2Details.getMessageCount());
        assertEquals(2, chatRoom2Details.getMemberCount());
        assertTrue(chatRoom2Details.getMemberEntityList().contains(memberAEntity));
    }
}
