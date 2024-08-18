package com.example.runningservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateJoinApplyDto {
    @NotNull
    Long joinApplyId;
    @NotNull
    @Size(max = 100)
    String message;
}
