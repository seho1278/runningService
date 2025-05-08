package com.example.runningservice.dto.regular_run;

import com.example.runningservice.enums.Region;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

@Getter
public class RegularRunRequestDto {

    @NotNull
    private int week;
    @NotNull
    private int count;
    @NotNull
    private List<String> dayOfWeek;
    @NotNull
    private Region activityRegion;
    private LocalTime time;
}
