package com.example.runningservice.dto.runRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunRecordResponseDto {
    private Long id;
    private Long userId;
    private Integer totalDistance;
    private String totalRunningTime;
    private String averagePace;
    private Integer distance;
    private Integer runningTime;
    private Duration pace;
    private Integer isPublic;
    private Integer runCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
