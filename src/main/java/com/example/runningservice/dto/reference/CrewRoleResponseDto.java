package com.example.runningservice.dto.reference;

import com.example.runningservice.enums.CrewRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewRoleResponseDto {

    private Long crewId;
    private CrewRole role;
}
