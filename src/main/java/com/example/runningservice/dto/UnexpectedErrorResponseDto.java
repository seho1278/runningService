package com.example.runningservice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UnexpectedErrorResponseDto {
    private final String message;
}
