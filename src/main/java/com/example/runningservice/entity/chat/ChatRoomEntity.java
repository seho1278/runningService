package com.example.runningservice.entity.chat;

import com.example.runningservice.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private String roomType;

    @ElementCollection
    private Set<String> sessions = new HashSet<>();

    private LocalDateTime lastReadAt;

    public ChatRoomEntity(String roomName, String roomType) {
        this.roomName = roomName;
        this.roomType = roomType;
    }

}
