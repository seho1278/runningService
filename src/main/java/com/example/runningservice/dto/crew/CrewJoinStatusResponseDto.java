package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Region;
import com.example.runningservice.util.S3FileUtil;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
public class CrewJoinStatusResponseDto extends CrewBaseResponseDto {

    private boolean isJoined;

    public static CrewJoinStatusResponseDto fromEntity(CrewEntity crewEntity, boolean isJoined,
        S3FileUtil s3FileUtil) {
        return CrewJoinStatusResponseDto.builder()
            .crewId(crewEntity.getId())
            .leader(crewEntity.getLeader().getNickName())
            .crewName(crewEntity.getCrewName())
            .crewImage(s3FileUtil.createPresignedUrl(crewEntity.getCrewImage()))
            .crewCapacity(crewEntity.getCrewCapacity())
            .crewOccupancy(Optional.ofNullable(crewEntity.getCrewMember())
                .map(List::size)
                .orElse(0))
            .activityRegion(Optional.ofNullable(crewEntity.getActivityRegion())
                .map(Region::getRegionName)
                .orElse(null))
            .isJoined(isJoined)
            .build();
    }
}
