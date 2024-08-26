package com.example.runningservice.dto.runGoal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunGoalRequestDto {
    private Long userId;
    private Integer totalDistance;
    private String totalRunningTime;
    private String averagePace;
    private Integer isPublic;
    private Integer runCount;
}
