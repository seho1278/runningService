package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
public class CrewDetailResponseDto extends CrewBaseResponseDto {

    private String description;
    private int runningCount;
    private Limit limit;

    public static CrewDetailResponseDto fromEntity(CrewEntity crewEntity) {
        return CrewDetailResponseDto.builder()
            .crewId(crewEntity.getId())
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
            .leader(crewEntity.getLeader().getNickName())
            .crewOccupancy(crewEntity.getCrewMember().size())
            .build();
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
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
