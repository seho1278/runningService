package com.example.runningservice.entity.chat;

import com.example.runningservice.entity.BaseEntity;
import com.example.runningservice.enums.Message;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
public class MessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Message messageType;
    private String content;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "chat_join_id")
    private ChatJoinEntity chatJoin;

    public String getSender() {
        return this.chatJoin.getMember().getNickName();
    }

    public Long getRoomId() {
        return this.chatJoin.getChatRoom().getId();
    }

    public void editContent(String content) {
        this.content = content;
    }

    public void setChatJoinNull(ChatJoinEntity chatJoin) {
        this.chatJoin = chatJoin;
    }

}
