package com.example.runningservice.dto.runRecord;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunRecordRequestDto {
    private Long userId;
    private Long goalId;
    private Integer distance;
    private Integer runningTime;
    private Duration pace;
    private Integer isPublic;
}
