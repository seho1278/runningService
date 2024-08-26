package com.example.runningservice.dto.runRecord;

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
    private LocalDateTime runningTime;
    private LocalDateTime pace;
    private Integer isPublic;
    private Integer runCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
