package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangedLeaderResponseDto {

    private String oldLeaderNickName;
    private CrewRole oldLeaderRole;
    private String newLeaderNickName;
    private CrewRole newLeaderRole;

    public static ChangedLeaderResponseDto of(CrewMemberEntity oldLeader, CrewMemberEntity newLeader) {
        return ChangedLeaderResponseDto.builder()
            .oldLeaderNickName(oldLeader.getMember().getNickName())
            .oldLeaderRole(oldLeader.getRole())
            .newLeaderNickName(newLeader.getMember().getNickName())
            .newLeaderRole(newLeader.getRole())
            .build();
    }
}
