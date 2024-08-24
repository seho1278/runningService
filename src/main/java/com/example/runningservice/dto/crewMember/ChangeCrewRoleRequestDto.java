package com.example.runningservice.dto.crewMember;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.util.validator.CrewRoleValid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeCrewRoleRequestDto {

    private Long crewMemberId;
    @NotNull
    @CrewRoleValid(roles = {"STAFF", "MEMBER"})
    private CrewRole newRole;

}
