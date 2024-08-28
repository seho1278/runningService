package com.example.runningservice.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotValidResponseDto {

    List<Message> data;

    public NotValidResponseDto() {
        data = new ArrayList<>();
    }

    public void addErrorMessage(Message message) {
        data.add(message);
    }

    @Builder
    @Getter
    public static class Message {

        String field;
        String message;
    }
}
