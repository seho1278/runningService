package com.example.runningservice.dto.runGoal;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunGoalResponseDto {
    private Long id;
    private Long userId;
    private Integer totalDistance;
    private String totalRunningTime;
    private String averagePace;
    private Integer isPublic;
    private Integer runCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
