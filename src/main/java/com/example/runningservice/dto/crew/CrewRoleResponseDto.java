package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Region;
import com.example.runningservice.util.S3FileUtil;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Getter
public class CrewRoleResponseDto extends CrewBaseResponseDto {

    private CrewRole role;

    public static CrewRoleResponseDto fromEntity(CrewMemberEntity crewMemberEntity,
        S3FileUtil s3FileUtil) {
        CrewEntity crewEntity = crewMemberEntity.getCrew();

        return CrewRoleResponseDto.builder()
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
            .role(crewMemberEntity.getRole())
            .build();
    }
}
