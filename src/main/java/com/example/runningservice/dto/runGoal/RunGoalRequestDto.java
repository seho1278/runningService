package com.example.runningservice.dto.runGoal;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunGoalRequestDto {
    private Long userId;
    private Integer totalDistance;
    private Integer totalRunningTime;
    private Duration averagePace;
    private Integer isPublic;
    private Integer runCount;
}
