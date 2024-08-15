package com.example.runningservice.dto.chat;

import com.example.runningservice.enums.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {
    private Message type;
    private Long roomId;
    private String sender;
    private String contents;
}
