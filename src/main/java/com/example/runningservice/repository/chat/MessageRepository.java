package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.MessageEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.chatJoin = :chatJoin AND m.createdAt > :readAt")
    int countMessagesAfterReadAt(@Param("chatJoin") ChatJoinEntity chatJoin, @Param("readAt") LocalDateTime readAt);

}
