package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.util.S3FileUtil;
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
    private boolean isJoined;

    public static CrewDetailResponseDto fromEntity(CrewEntity crewEntity, boolean isJoined,
        ActivityRepository activityRepository, S3FileUtil s3FileUtil) {
        return CrewDetailResponseDto.builder()
            .crewId(crewEntity.getId())
            .crewName(crewEntity.getCrewName())
            .crewImage(s3FileUtil.createPresignedUrl(crewEntity.getCrewImage()))
            .description(crewEntity.getDescription())
            .crewCapacity(crewEntity.getCrewCapacity())
            .activityRegion(Optional.ofNullable(crewEntity.getActivityRegion())
                .map(Region::getRegionName)
                .orElse(null))
            .limit(Limit.builder()
                .gender(crewEntity.getGender())
                .leaderRequired(crewEntity.getLeaderRequired())
                .maxYear(crewEntity.getMaxYear())
                .minYear(crewEntity.getMinYear())
                .runRecordOpen(crewEntity.getRunRecordOpen())
                .build())
            .leader(crewEntity.getLeader().getNickName())
            .crewOccupancy(crewEntity.getCrewMember().size())
            .runningCount(activityRepository.countByCrew_Id(crewEntity.getId()))
            .isJoined(isJoined)
            .build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @Getter
    public static class Limit {

        private Gender gender;
        private Integer minYear;
        private Integer maxYear;
        private boolean leaderRequired;
        private boolean runRecordOpen;
    }
}
