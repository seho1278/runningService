package com.example.runningservice.service.chat;

import com.example.runningservice.dto.chat.ChatRoomDetailsDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.entity.chat.MessageEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.example.runningservice.repository.chat.MessageRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatJoinRepository chatJoinRepository;
    private final MessageRepository messageRepository;

    public void createChatRoom(Long crewId, String roomName, ChatRoom roomType) {
        CrewEntity crewEntity = crewRepository.findCrewById(crewId);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .roomName(roomName)
            .roomType(roomType)
            .crew(crewEntity)
            .build();

        chatRoomRepository.save(chatRoomEntity);
    }

    // 크루 채팅방 리스트 조회
    public Map<ChatRoomEntity, ChatRoomDetailsDto> getCrewChatRoomListForMember(Long crewId, Long memberId) {
        CrewEntity crewEntity = crewRepository.findCrewById(crewId);

        MemberEntity memberEntity = memberRepository.findMemberById(memberId);

        // 멤버가 참여중인 크루 채팅방 리스트 조회
        List<ChatJoinEntity> chatJoinList = chatJoinRepository.findByMemberAndChatRoom_Crew(memberEntity, crewEntity);

        return chatJoinList.stream()
            .collect(Collectors.toMap(
                ChatJoinEntity::getChatRoom,
                chatJoinEntity -> {
                    ChatRoomEntity chatRoomEntity = chatJoinEntity.getChatRoom();
//                    List<MemberEntity> memberList = chatJoinRepository.findMemberNicknamesByChatRoom(chatRoomEntity);
                    int messageCount = messageRepository.countMessagesAfterReadAt(chatJoinEntity.getId());
                    log.info("messageCount: {}", messageCount);

                    int memberCount = chatJoinRepository.countByChatRoom(chatRoomEntity);

                    return ChatRoomDetailsDto.builder()
                        .messageCount(messageCount)
                        .memberCount(memberCount)
//                        .memberEntityList(memberList)
                        .build();
                }
            ));
    }

    // 1:1 채팅방 생성
    public void createPersonalChatRoom(Long crewId, Long memberAId, Long memberBId) {
        // 크루 확인
        CrewEntity crewEntity = crewRepository.findCrewById(crewId);
        // 사용자 확인
        MemberEntity memberAEntity = memberRepository.findMemberById(memberAId);
        MemberEntity memberBEntity = memberRepository.findMemberById(memberBId);

        // 채팅방 생성
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .roomName("Psersonal Chat")
            .roomType(ChatRoom.PERSONAL)
            .crew(crewEntity)
            .build();

        ChatRoomEntity savedChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

        // 채팅방 참여
        joinChatRoom(crewId, savedChatRoomEntity.getId(), memberAEntity.getId());
        joinChatRoom(crewId, savedChatRoomEntity.getId(), memberBEntity.getId());
    }

    // 채팅방 참여
    public void joinChatRoom(Long crewId, Long roomId, Long memberId) {
        // 크루 확인
        CrewEntity crewEntity = crewRepository.findCrewById(crewId);
        // 사용자 확인
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);
        // chatroom 확인
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findChatRoomById(roomId);

        // 사용자가 해당 크루에 가입되어 있는지 확인
        validateCrewMember(crewEntity, memberEntity);

        // 중복 참여 확인
        if (chatJoinRepository.existsByChatRoomAndMember(chatRoomEntity, memberEntity)) {
            throw new RuntimeException("이미 채팅방에 참여중인 멤버입니다.");
        }

        // 채팅방 참여
        ChatJoinEntity chatJoinEntity = new ChatJoinEntity();

        // (crew 채팅방 or 1:1 채팅방)인지 crew staff 채팅방인지 확인
        if (chatRoomEntity.getRoomType().equals(ChatRoom.CREW) || chatRoomEntity.getRoomType().equals(ChatRoom.PERSONAL)) {
            chatJoinEntity.addMemberChatRoom(chatRoomEntity, memberEntity);
        } else if (chatRoomEntity.getRoomType().equals(ChatRoom.CREW_STAFF)) {
            // 멤버가 crew의 staff인지 확인
            validateCrewLeaderOrStaff(crewEntity, memberEntity);
            chatJoinEntity.addMemberChatRoom(chatRoomEntity, memberEntity);
        }

        chatJoinRepository.save(chatJoinEntity);

        // 채팅방 입장
        enterChatRoom(crewId, roomId, memberId);
    }

    // 채팅방 입장
    public void enterChatRoom(Long crewId, Long roomId, Long memberId) {
        // 사용자 확인
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);
        // 채팅방 확인
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findChatRoomById(roomId);
        // 참여확인
        ChatJoinEntity chatJoinEntity = chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberEntity);
        if (chatJoinEntity == null) {
            throw new RuntimeException("멤버가 채팅방에 참여중이지 않습니다.");
        }

        // read_at 업데이트
        chatJoinEntity.enterMemberChatRoom();

        chatJoinRepository.save(chatJoinEntity);
    }

    // 채팅방 퇴장
    public void leaveChatRoom(Long crewId, Long roomId, Long memberId) {
        // 사용자가 채팅방에 있는지 확인
        ChatJoinEntity chatJoinEntity = chatJoinRepository.findByChatRoom_IdAndMember_Id(roomId, memberId);
        if (chatJoinEntity == null) {
            throw new RuntimeException("멤버가 채팅방에 참여중이지 않습니다.");
        }

        List<MessageEntity> messages = messageRepository.findByChatJoin(chatJoinEntity);
        for (MessageEntity message : messages) {
            message.setChatJoinNull(null);
        }

        // 채팅방 퇴장
        chatJoinRepository.delete(chatJoinEntity);
    }

    // 채팅방 강제퇴장
    public void ejectionChatRoom(Long crewId, Long roomId, Long memberId, Long adminId) {
        // 크루 확인
        CrewEntity crewEntity = crewRepository.findCrewById(crewId);
        // 채팅방 확인
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findChatRoomById(roomId);
        // 사용자 확인
        MemberEntity memberEntity = memberRepository.findMemberById(memberId);
        MemberEntity adminEntity = memberRepository.findMemberById(adminId);

        // 채팅방 참여중인지 확인
        ChatJoinEntity memberChatJoinEntity = chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, memberEntity);
        ChatJoinEntity adminChatJoinEntity = chatJoinRepository.findByChatRoomAndMember(chatRoomEntity, adminEntity);
        if (memberChatJoinEntity == null || adminEntity == null) {
            throw new RuntimeException("멤버가 채팅방에 참여중이지 않습니다.");
        }

        // 크루 운영자 권한 확인
        validateCrewLeaderOrStaff(crewEntity, adminEntity);

        // 채팅방 강제퇴장
        chatJoinRepository.delete(memberChatJoinEntity);
    }

    // 크루 멤버인지 확인
    public void validateCrewMember(CrewEntity crew, MemberEntity member) {
        if (!crewMemberRepository.findByCrewAndMember(crew, member).isPresent()) {
            throw new RuntimeException("해당 사용자가 크루에 가입되어 있지 않습니다.");
        }
    }

    public void validateCrewLeaderOrStaff(CrewEntity crew, MemberEntity member) {
        List<CrewRole> crewRoles = Arrays.asList(CrewRole.LEADER, CrewRole.STAFF);
        if (!crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, crewRoles).isPresent()) {
            throw new RuntimeException("해당 멤버는 운영진이 아닙니다.");
        }
    }
}
