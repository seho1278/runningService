package com.example.runningservice.dto.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Region;
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
public class CrewRoleResponseDto extends CrewBaseResponseDto {

    private CrewRole role;

    public static CrewRoleResponseDto fromEntity(CrewEntity crewEntity, CrewRole crewRole) {
        return CrewRoleResponseDto.builder()
            .crewId(crewEntity.getId())
            .leader(crewEntity.getLeader().getNickName())
            .crewName(crewEntity.getCrewName())
            .crewImage(crewEntity.getCrewImage())
            .crewCapacity(crewEntity.getCrewCapacity())
            .crewOccupancy(Optional.ofNullable(crewEntity.getCrewMember())
                .map(List::size)
                .orElse(0))
            .activityRegion(Optional.ofNullable(crewEntity.getActivityRegion())
                .map(Region::getRegionName)
                .orElse(null))
            .role(crewRole)
            .build();
    }
}
