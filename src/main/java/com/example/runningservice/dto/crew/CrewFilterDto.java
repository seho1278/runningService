package com.example.runningservice.dto.crew;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.OccupancyStatus;
import com.example.runningservice.enums.ParticipateType;
import com.example.runningservice.enums.Region;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;

public class CrewFilterDto {

    @Getter
    @Builder
    public static class Participate {

        private Long userId;
        private ParticipateType filter;

        public void setFilter(String filter) {
            if (filter != null && Arrays.stream(ParticipateType.values())
                .map(Enum::name)
                .anyMatch(name -> name.contains(filter))) {
                this.filter = ParticipateType.valueOf(filter);
            }
        }
    }

    @Getter
    @Builder
    public static class CrewInfo {

        private Region activityRegion;
        private Integer minAge;
        private Integer maxAge;
        private Gender gender;
        private Boolean runRecordPublic;
        private Boolean leaderRequired;
        private OccupancyStatus occupancyStatus;
    }
}
