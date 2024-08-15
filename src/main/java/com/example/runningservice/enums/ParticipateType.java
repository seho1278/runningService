package com.example.runningservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ParticipateType {
    HOST(CrewRole.LEADER),
    JOIN(CrewRole.MEMBER);

    private final CrewRole crewRole;
}
