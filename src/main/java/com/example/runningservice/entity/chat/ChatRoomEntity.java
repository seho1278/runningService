package com.example.runningservice.entity.chat;

import com.example.runningservice.entity.BaseEntity;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.ChatRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class ChatRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    private ChatRoom roomType;
    private LocalDateTime lastReadAt;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private CrewEntity crew;


    @Builder
    public ChatRoomEntity(Long id, String roomName, ChatRoom roomType, CrewEntity crew) {
        this.id = id;
        this.roomName = roomName;
        this.roomType = roomType;
        this.crew = crew;
    }

}
