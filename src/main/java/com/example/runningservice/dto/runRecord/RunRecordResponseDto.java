package com.example.runningservice.dto.runRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunRecordResponseDto {
    private Long id;
    private Long userId;
    private Double distance;
    private Integer runningTime;
    private Integer pace;
    private Integer isPublic;
    private LocalDate runningDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
