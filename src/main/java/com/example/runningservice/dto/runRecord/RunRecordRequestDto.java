package com.example.runningservice.dto.runRecord;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunRecordRequestDto {
    private Long goalId;
    private Double distance = 0.0;
    private String runningTime = "00:00:00";
    private String pace = "00:00";
    private LocalDateTime runningDate;
}
