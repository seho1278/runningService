package com.example.runningservice.dto.crewMember;

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
}
