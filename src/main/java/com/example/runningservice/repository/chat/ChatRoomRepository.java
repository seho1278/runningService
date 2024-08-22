package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

//    List<ChatRoomEntity> findByCrewId(Long crewId);

    default ChatRoomEntity findChatRoomById(Long roomId) {
        return findById(roomId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHATROOM));
    }
}
