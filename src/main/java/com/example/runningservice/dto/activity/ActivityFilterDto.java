package com.example.runningservice.dto.activity;

import com.example.runningservice.enums.ActivityCategory;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ActivityFilterDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private ActivityCategory category;
}
