package com.example.runningservice.dto.runRecord;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunRecordRequestDto {
    private Long userId;
    private Long goalId;
    private Double distance;
    private String runningTime;
    private String pace;
    private Integer isPublic;
}
