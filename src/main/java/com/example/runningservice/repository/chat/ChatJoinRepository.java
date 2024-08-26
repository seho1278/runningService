package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatJoinRepository extends JpaRepository<ChatJoinEntity, Long>,
    ChatJoinRepositoryCustom {
    // 중복참여 확인
    boolean existsByChatRoomAndMember(ChatRoomEntity chatRoom, MemberEntity member);

    // 특정 채팅방의 멤버 확인
    ChatJoinEntity findByChatRoomAndMember(ChatRoomEntity chatRoom, MemberEntity member);
    ChatJoinEntity findByChatRoom_IdAndMember_Id(Long roomId, Long memberId);


    // 멤버가 참여중인 크루 chatRoom 확인
    List<ChatJoinEntity> findByMemberAndChatRoom_Crew(MemberEntity member, CrewEntity crew);

    // 참여중인 멤버 수
    int countByChatRoom(ChatRoomEntity chatRoom);

//    @Query("SELECT m.nickName FROM ChatJoinEntity cj JOIN cj.member m WHERE cj.chatRoom = :chatRoom")
//    List<MemberEntity> findMemberNicknamesByChatRoom(@Param("chatRoom") ChatRoomEntity chatRoom);

    List<ChatJoinEntity> findByChatRoom_Id(Long chatRoomId);

}
