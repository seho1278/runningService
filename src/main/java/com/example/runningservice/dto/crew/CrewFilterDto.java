package com.example.runningservice.dto.crew;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.OccupancyStatus;
import com.example.runningservice.enums.Region;
import lombok.Builder;
import lombok.Getter;

public class CrewFilterDto {

    @Getter
    @Builder
    public static class CrewInfo {

        private Region activityRegion;
        private CrewRole role;
        private Integer minAge;
        private Integer maxAge;
        private Gender gender;
        private Boolean runRecordPublic;
        private Boolean leaderRequired;
        private OccupancyStatus occupancyStatus;
    }
}
