package com.example.runningservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE(0),
    FEMALE(1);

    private final int code;

    public int getCode() {
        return code;
    }

    public static Gender fromCode(int code) {
        return code == 0 ? MALE : FEMALE;
    }
}
