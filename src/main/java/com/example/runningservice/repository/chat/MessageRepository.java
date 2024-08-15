package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.chat.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
