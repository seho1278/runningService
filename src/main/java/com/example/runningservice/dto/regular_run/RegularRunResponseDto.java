package com.example.runningservice.dto.regular_run;

import com.example.runningservice.entity.RegularRunMeetingEntity;
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
    private Frequency frequency;
    private List<String> weekdays;
    private String location;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Frequency {

        private int weeks;
        private int times;
    }

    public static RegularRunResponseDto fromEntity(
        RegularRunMeetingEntity regularRunMeetingEntity) {

        return RegularRunResponseDto.builder()
            .id(regularRunMeetingEntity.getId())
            .frequency(Frequency.builder()
                .weeks(regularRunMeetingEntity.getWeek())
                .times(regularRunMeetingEntity.getCount())
                .build())
            .weekdays(regularRunMeetingEntity.getDayOfWeek())
            .location(regularRunMeetingEntity.getActivityRegion().getRegionName())
            .build();
    }
}
