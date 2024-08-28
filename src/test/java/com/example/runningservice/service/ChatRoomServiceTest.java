package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.chat.ChatRoomDetailsDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.example.runningservice.repository.chat.MessageRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.service.chat.ChatRoomService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            .id(1L)
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
        when(crewRepository.findCrewById(crewEntity.getId())).thenReturn(crewEntity);

        ChatRoomEntity mockChatRoomEntity = ChatRoomEntity.builder()
            .roomName(crewEntity.getCrewName())
            .roomType(ChatRoom.CREW)
            .crew(crewEntity)
            .build();

        when(chatRoomRepository.save(any(ChatRoomEntity.class))).thenReturn(mockChatRoomEntity);

        // When
        chatRoomService.createChatRoom(crewEntity.getId(), crewEntity.getCrewName(), ChatRoom.CREW);

        // Then
        ArgumentCaptor<ChatRoomEntity> chatRoomEntityCaptor = ArgumentCaptor.forClass(ChatRoomEntity.class);
        verify(chatRoomRepository, times(1)).save(chatRoomEntityCaptor.capture());

        ChatRoomEntity capturedChatRoomEntity = chatRoomEntityCaptor.getValue();

        assertEquals(crewEntity.getCrewName(), capturedChatRoomEntity.getRoomName());
        assertEquals(ChatRoom.CREW, capturedChatRoomEntity.getRoomType());
        assertEquals(crewEntity, capturedChatRoomEntity.getCrew());
    }

//    @Test
//    public void testGetCrewChatRoomListForMember_success() {
//        Long crewId = 1L;
//        Long memberAId = 1L;
//        Long memberBId = 2L;
//
//        ChatRoomEntity chatRoom1 = ChatRoomEntity.builder()
//            .id(1L)
//            .roomName(crewEntity.getCrewName())
//            .roomType(ChatRoom.CREW)
//            .crew(crewEntity)
//            .build();
//
//        ChatRoomEntity chatRoom2 = ChatRoomEntity.builder()
//            .id(2L)
//            .roomName("Personal Chat")
//            .roomType(ChatRoom.PERSONAL)
//            .crew(crewEntity)
//            .build();
//
//        ChatJoinEntity chatJoin1 = ChatJoinEntity.builder()
//            .id(1L)
//            .chatRoom(chatRoom1)
//            .member(memberAEntity)
//            .joinedAt(LocalDateTime.now().minusDays(1))
//            .readAt(LocalDateTime.now().minusHours(2))
//            .build();
//
//        ChatJoinEntity chatJoin2 = ChatJoinEntity.builder()
//            .id(2L)
//            .chatRoom(chatRoom2)
//            .member(memberAEntity)
//            .joinedAt(LocalDateTime.now().minusDays(1))
//            .readAt(LocalDateTime.now().minusHours(2))
//            .build();
//
//        ChatJoinEntity chatJoin3 = ChatJoinEntity.builder()
//            .id(3L)
//            .chatRoom(chatRoom1)
//            .member(memberBEntity)
//            .joinedAt(LocalDateTime.now().minusDays(1))
//            .readAt(LocalDateTime.now().minusHours(2))
//            .build();
//
//        ChatJoinEntity chatJoin4 = ChatJoinEntity.builder()
//            .id(4L)
//            .chatRoom(chatRoom1)
//            .member(memberBEntity)
//            .joinedAt(LocalDateTime.now().minusDays(1))
//            .readAt(LocalDateTime.now().minusHours(2))
//            .build();
//
//        when(crewRepository.findCrewById(crewId)).thenReturn(crewEntity);
//        when(memberRepository.findMemberById(memberAId)).thenReturn(memberAEntity);
//        when(chatJoinRepository.findByMemberAndChatRoom_Crew(memberAEntity, crewEntity))
//            .thenReturn(Arrays.asList(chatJoin1, chatJoin2));
//        when(chatJoinRepository.findMemberNicknamesByChatRoom(chatRoom1))
//            .thenReturn(Arrays.asList(memberAEntity, memberBEntity));
//        when(chatJoinRepository.findMemberNicknamesByChatRoom(chatRoom2))
//            .thenReturn(Arrays.asList(memberAEntity, memberBEntity));
//        when(messageRepository.countMessagesAfterReadAt(eq(chatJoin1), any(LocalDateTime.class))).thenReturn(5);
//        when(messageRepository.countMessagesAfterReadAt(eq(chatJoin2), any(LocalDateTime.class))).thenReturn(2);
//        when(chatJoinRepository.countByChatRoom(chatRoom1)).thenReturn(2);
//        when(chatJoinRepository.countByChatRoom(chatRoom2)).thenReturn(2);
//
//        // when
//        Map<ChatRoomEntity, ChatRoomDetailsDto> result = chatRoomService.getCrewChatRoomListForMember(crewId, memberAId);
//
//        // then
//        verify(crewRepository).findCrewById(crewId);
//        verify(memberRepository).findMemberById(memberAId);
//        verify(chatJoinRepository).findByMemberAndChatRoom_Crew(memberAEntity, crewEntity);
//        verify(chatJoinRepository).findMemberNicknamesByChatRoom(chatRoom1);
//        verify(chatJoinRepository).findMemberNicknamesByChatRoom(chatRoom2);
//        verify(messageRepository).countMessagesAfterReadAt(chatJoin1, chatJoin1.getReadAt());
//        verify(messageRepository).countMessagesAfterReadAt(chatJoin2, chatJoin2.getReadAt());
//        verify(chatJoinRepository).countByChatRoom(chatRoom1);
//        verify(chatJoinRepository).countByChatRoom(chatRoom2);
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        ChatRoomDetailsDto chatRoom1Details = result.get(chatRoom1);
//        assertNotNull(chatRoom1Details);
//        assertEquals(5, chatRoom1Details.getMessageCount());
//        assertEquals(2, chatRoom1Details.getMemberCount());
//        assertTrue(chatRoom1Details.getMemberEntityList().containsAll(Arrays.asList(memberAEntity, memberBEntity)));
//
//        ChatRoomDetailsDto chatRoom2Details = result.get(chatRoom2);
//        assertNotNull(chatRoom2Details);
//        assertEquals(2, chatRoom2Details.getMessageCount());
//        assertEquals(2, chatRoom2Details.getMemberCount());
//        assertTrue(chatRoom2Details.getMemberEntityList().contains(memberAEntity));
//    }

    @Test
    public void createPersonalChatRoom_success() {
//        // given
//        when(crewRepository.findCrewById(crewEntity.getCrewId())).thenReturn(crewEntity);
//        when(memberRepository.findMemberById(memberAEntity.getId())).thenReturn(memberAEntity);
//        when(memberRepository.findMemberById(memberBEntity.getId())).thenReturn(memberBEntity);
//
//        ChatRoomEntity mockChatRoomEntity = ChatRoomEntity.builder()
//            .id(1L)
//            .roomName("Personal Chat")
//            .roomType(ChatRoom.PERSONAL)
//            .crew(crewEntity)
//            .build();
//
//        when(chatRoomRepository.save(any(ChatRoomEntity.class))).thenReturn(mockChatRoomEntity);
//        when(chatRoomRepository.findChatRoomById(mockChatRoomEntity.getId())).thenReturn(mockChatRoomEntity);
//
//        when(crewMemberRepository.findByCrewAndMember(crewEntity, memberAEntity))
//            .thenReturn(Optional.of(crewMemberAEntity));
//        when(crewMemberRepository.findByCrewAndMember(crewEntity, memberBEntity))
//            .thenReturn(Optional.of(crewMemberBEntity));
//
//        // when
//        chatRoomService.createPersonalChatRoom(crewEntity.getCrewId(), memberAEntity.getId(), memberBEntity.getId());
//
//        // then
//        verify(chatRoomRepository, times(1)).save(any(ChatRoomEntity.class));
//        verify(chatJoinRepository, times(2)).save(any(ChatJoinEntity.class));
    }

    @Test
    public void joinChatRoom_success() {
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomName("Test Room")
            .roomType(ChatRoom.PERSONAL)
            .crew(crewEntity)
            .build();

        when(crewRepository.findCrewById(crewEntity.getId())).thenReturn(crewEntity);
        when(memberRepository.findMemberById(memberAEntity.getId())).thenReturn(memberAEntity);
        when(chatRoomRepository.findChatRoomById(chatRoomEntity.getId())).thenReturn(chatRoomEntity);
        when(chatJoinRepository.existsByChatRoomAndMember(chatRoomEntity, memberAEntity)).thenReturn(false);

        ChatJoinEntity chatJoinEntity = new ChatJoinEntity();
        when(chatJoinRepository.save(any(ChatJoinEntity.class))).thenReturn(chatJoinEntity);

        when(crewMemberRepository.findByCrewAndMember(crewEntity, memberAEntity))
            .thenReturn(Optional.of(crewMemberAEntity));

        when(chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberAEntity)).thenReturn(chatJoinEntity);

        // When
        chatRoomService.joinChatRoom(crewEntity.getId(), chatRoomEntity.getId(), memberAEntity.getId());

        // Then
        verify(crewRepository, times(1)).findCrewById(crewEntity.getId());
        verify(memberRepository, times(2)).findMemberById(memberAEntity.getId());
        verify(chatRoomRepository, times(2)).findChatRoomById(chatRoomEntity.getId());
        verify(chatJoinRepository, times(1)).existsByChatRoomAndMember(chatRoomEntity, memberAEntity);
        verify(chatJoinRepository, times(1)).save(chatJoinEntity);
    }

    @Test
    public void enterChatRoom_success() {
        // given
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomName("Personal Chat")
            .roomType(ChatRoom.PERSONAL)
            .crew(crewEntity)
            .build();

        ChatJoinEntity chatJoinEntity = new ChatJoinEntity();
        when(memberRepository.findMemberById(memberAEntity.getId())).thenReturn(memberAEntity);
        when(chatRoomRepository.findChatRoomById(chatRoomEntity.getId())).thenReturn(chatRoomEntity);
        when(chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberAEntity)).thenReturn(chatJoinEntity);

        // when
        chatRoomService.enterChatRoom(crewEntity.getId(), chatRoomEntity.getId(), memberAEntity.getId());

        // then
        verify(chatJoinRepository, times(1)).save(chatJoinEntity);
    }

    @Test
    public void leaveChatRoom_success() {
        // Given
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomName("Personal Chat")
            .roomType(ChatRoom.PERSONAL)
            .crew(crewEntity)
            .build();

        ChatJoinEntity chatJoinEntity = new ChatJoinEntity();
        when(memberRepository.findMemberById(memberAEntity.getId())).thenReturn(memberAEntity);
        when(chatRoomRepository.findChatRoomById(chatRoomEntity.getId())).thenReturn(chatRoomEntity);
        when(chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberAEntity)).thenReturn(chatJoinEntity);

        doNothing().when(chatJoinRepository).delete(any(ChatJoinEntity.class));

        // When
        chatRoomService.leaveChatRoom(crewEntity.getId(), chatRoomEntity.getId(), memberAEntity.getId());

        // Then
        verify(chatJoinRepository, times(1)).delete(chatJoinEntity);
    }

    @Test
    public void ejectionChatRoom_success() {
        // given
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomName("Crew Chat")
            .roomType(ChatRoom.CREW)
            .crew(crewEntity)
            .build();

        when(crewRepository.findCrewById(crewEntity.getId())).thenReturn(crewEntity);
        when(chatRoomRepository.findChatRoomById(chatRoomEntity.getId())).thenReturn(chatRoomEntity);
        when(memberRepository.findMemberById(memberAEntity.getId())).thenReturn(memberAEntity);
        when(memberRepository.findMemberById(memberBEntity.getId())).thenReturn(memberBEntity);

        List<CrewRole> crewRoles = Arrays.asList(CrewRole.LEADER, CrewRole.STAFF);

        when(crewMemberRepository.findByCrewAndMemberAndRoleIn(crewEntity, memberAEntity, crewRoles))
            .thenReturn(Optional.of(crewMemberAEntity));

        ChatJoinEntity adminChatJoinEntity = new ChatJoinEntity();
        ChatJoinEntity memberChatJoinEntity = new ChatJoinEntity();

        when(chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberAEntity))
            .thenReturn(adminChatJoinEntity);
        when(chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberBEntity))
            .thenReturn(memberChatJoinEntity);
        doNothing().when(chatJoinRepository).delete(memberChatJoinEntity);

        // when
        chatRoomService.ejectionChatRoom(crewEntity.getId(), chatRoomEntity.getId(), memberBEntity.getId(), memberAEntity.getId());

        // then
        verify(chatJoinRepository, times(1)).delete(memberChatJoinEntity);
    }

}
