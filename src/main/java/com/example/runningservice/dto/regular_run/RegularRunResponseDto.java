package com.example.runningservice.dto.regular_run;

import com.example.runningservice.entity.RegularRunMeetingEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegularRunResponseDto {

    private Long id;
    private int week;
    private int count;
    private List<String> dayOfWeek;
    private String activityRegion;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    public static RegularRunResponseDto fromEntity(
        RegularRunMeetingEntity regularRunMeetingEntity) {

        return RegularRunResponseDto.builder()
            .id(regularRunMeetingEntity.getId())
            .week(regularRunMeetingEntity.getWeek())
            .count(regularRunMeetingEntity.getCount())
            .dayOfWeek(regularRunMeetingEntity.getDayOfWeek())
            .activityRegion(regularRunMeetingEntity.getActivityRegion().getRegionName())
            .time(regularRunMeetingEntity.getTime())
            .build();
    }
}
