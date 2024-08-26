package com.example.runningservice.dto.chat;

import com.example.runningservice.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatRoomDetailsDto {
    private long memberCount;
    private int messageCount;
//    private List<MemberEntity> memberEntityList;
}