package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.chatJoin.id = :chatJoinId AND" +
        " m.createdAt > (SELECT cj.readAt FROM ChatJoinEntity cj WHERE cj.id = :chatJoinId)")
    int countMessagesAfterReadAt(@Param("chatJoinId") Long chatJoinId);



    List<MessageEntity> findByChatJoin(ChatJoinEntity chatJoin);

}
