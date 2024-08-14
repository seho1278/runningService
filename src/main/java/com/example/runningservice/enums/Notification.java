package com.example.runningservice.enums;

public enum Notification {
    ON(0),
    OFF(1);

    private final int code;

    Notification(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Notification fromCode(int code) {
        return code == 0 ? ON : OFF;
    }
}