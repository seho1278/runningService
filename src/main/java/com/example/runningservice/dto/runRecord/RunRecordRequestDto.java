package com.example.runningservice.dto.runRecord;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private LocalDateTime runningTime;
    @NotNull
    private LocalDateTime pace;
    @NotNull
    private Integer isPublic;
}
