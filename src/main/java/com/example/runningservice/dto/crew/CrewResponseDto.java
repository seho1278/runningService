package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class CrewResponseDto {

    @AllArgsConstructor
    @SuperBuilder
    @Getter
    public static class Summary {

        List<CrewData> data;

        public Summary() {
            data = new ArrayList<>();
        }

        public void addCrew(CrewData crew) {
            data.add(crew);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @Getter
    public static class CrewData {

        private Long crewId;
        private String crewName;
        private String crewImage;
        private String leader;
        private Integer crewCapacity;
        private Integer crewOccupancy;
        private String activityRegion;

        public static CrewData fromEntity(CrewEntity crewEntity) {

            return CrewData.builder()
                .crewId(crewEntity.getCrewId())
                .leader(crewEntity.getMember().getNickName())
                .crewName(crewEntity.getCrewName())
                .crewImage(crewEntity.getCrewImage())
                .crewCapacity(crewEntity.getCrewCapacity())
                .crewOccupancy(crewEntity.getCrewMember().size())
                .activityRegion(Optional.ofNullable(crewEntity.getActivityRegion())
                    .map(Region::getRegionName)
                    .orElse(null))
                .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @Getter
    public static class Detail extends CrewData {

        private String description;
        private int runningCount;
        private Limit limit;

        public static Detail fromEntity(CrewEntity crewEntity) {
            return Detail.builder()
                .crewId(crewEntity.getCrewId())
                .crewName(crewEntity.getCrewName())
                .crewImage(crewEntity.getCrewImage())
                .description(crewEntity.getDescription())
                .crewCapacity(crewEntity.getCrewCapacity())
                .activityRegion(Optional.ofNullable(crewEntity.getActivityRegion())
                    .map(Region::getRegionName)
                    .orElse(null))
                .limit(Limit.builder()
                    .gender(crewEntity.getGender())
                    .leaderRequired(crewEntity.getLeaderRequired())
                    .maxAge(crewEntity.getMaxAge())
                    .minAge(crewEntity.getMinAge())
                    .runRecordOpen(crewEntity.getRunRecordOpen())
                    .build())
                .leader(crewEntity.getMember().getNickName())
                .crewOccupancy(crewEntity.getCrewMember().size())
                .build();
        }

        public void setRunningCount(int runningCount) {
            this.runningCount = runningCount;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @Getter
    public static class Limit {

        private Gender gender;
        private Integer minAge;
        private Integer maxAge;
        private boolean leaderRequired;
        private boolean runRecordOpen;
    }
}
