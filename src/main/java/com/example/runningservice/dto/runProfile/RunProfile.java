package com.example.runningservice.dto.runProfile;

import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.RunGoalEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RunProfile {
    private Double totalDistanceGoal;
    private Integer totalRunningTimeGoal;
    private Integer averagePaceGoal;
    private Integer runCountGoal;

    private Double totalDistance;
    private Integer totalRunningTime;
    private Integer averagePace;
    private Integer runCount;


    public static RunProfile of(RunGoalEntity runGoal,
        RunRecordResponseDto runRecord) {

        return RunProfile.builder()
            .totalDistanceGoal(runGoal.getTotalDistance())
            .runCountGoal(runGoal.getRunCount())
            .averagePaceGoal(runGoal.getAveragePace())
            .totalRunningTimeGoal(runGoal.getTotalRunningTime())
            .totalDistance(runRecord.getDistance())
            .totalRunningTime(runRecord.getRunningTime())
            .averagePace(runRecord.getPace())
            .runCount(runRecord.getRunCount())
            .build();
    }
}