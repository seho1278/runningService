package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class CrewResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @Getter
    public static class CrewData {

        private Long crewId;
        private String crewName;
        private String leader;
        private Integer crewCapacity;
        private Integer crewOccupancy;
        private Region activityRegion;

        public static CrewData fromEntityAndLeaderNameAndOccupancy(CrewEntity crewEntity,
            String nickname,
            int occupancy) {

            return CrewData.builder()
                .crewId(crewEntity.getCrewId())
                .leader(nickname)
                .crewName(crewEntity.getCrewName())
                .crewCapacity(crewEntity.getCrewCapacity())
                .crewOccupancy(occupancy)
                .activityRegion(crewEntity.getActivityRegion())
                .build();
        }
    }
}
