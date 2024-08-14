package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
}
