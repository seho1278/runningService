package com.example.runningservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomErrorResponseDto {
    private String errorCode;
    private String message;
}
