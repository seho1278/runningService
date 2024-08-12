package com.example.runningservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendingMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}
