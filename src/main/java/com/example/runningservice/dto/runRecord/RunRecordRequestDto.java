package com.example.runningservice.dto.runRecord;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunRecordRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long goalId;
    @NotNull
    private Integer distance;
    @NotNull
    private Duration runningTime;
    @NotNull
    private Duration pace;
    @NotNull
    private Integer isPublic;
}
