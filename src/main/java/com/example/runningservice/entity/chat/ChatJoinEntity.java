package com.example.runningservice.entity.chat;

import com.example.runningservice.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatJoinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MemberEntity member;

    private LocalDateTime joinedAt;

    private LocalDateTime readAt;

//    @OneToMany(mappedBy = "chatJoin")
//    private List<MessageEntity> messageEntity;


    public void addMemberChatRoom(ChatRoomEntity chatRoom, MemberEntity member) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.joinedAt = LocalDateTime.now();
    }

    public void enterMemberChatRoom() {
        this.readAt = LocalDateTime.now();
    }

}
