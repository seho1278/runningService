package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeCrewRoleResponseDto {
    private String nickName;
    private String crewName;
    private CrewRole crewRole;

    public static ChangeCrewRoleResponseDto of(CrewMemberEntity crewMemberEntity) {
        return ChangeCrewRoleResponseDto.builder()
            .nickName(crewMemberEntity.getMember().getNickName())
            .crewName(crewMemberEntity.getCrew().getCrewName())
            .crewRole(crewMemberEntity.getRole())
            .build();
    }
}
