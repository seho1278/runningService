package com.example.runningservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CrewRole {
    LEADER(1), STAFF(2), MEMBER(3);

    private final int order;
}
