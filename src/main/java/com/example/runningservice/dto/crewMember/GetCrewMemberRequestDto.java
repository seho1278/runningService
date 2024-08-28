package com.example.runningservice.dto.crewMember;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.util.validator.YearRange;
import lombok.Builder;
import lombok.Getter;

public class GetCrewMemberRequestDto {

    @Getter
    @Builder
    @YearRange(minYear = "minAge", maxYear = "maxAge")
    public static class Filter {
        private Gender gender;
        private Integer minAge;
        private Integer maxAge;
        private CrewRole crewRole;
    }
}
