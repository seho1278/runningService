package com.example.runningservice.dto.runGoal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunGoalRequestDto {
    private Long userId;
    private Double totalDistance = 0.0;
    private String totalRunningTime = "00:00:00";
    private String averagePace = "00:00";
    private Integer runCount = 0;
}
