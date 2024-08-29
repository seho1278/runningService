package com.example.runningservice.dto.crewMember;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.util.validator.YearRange;
import lombok.Builder;
import lombok.Getter;

public class GetCrewMemberRequestDto {

    @Getter
    @Builder
    @YearRange(minYear = "minYear", maxYear = "maxYear")
    public static class Filter {
        private Gender gender;
        private Integer minYear;
        private Integer maxYear;
        private CrewRole crewRole;
    }
}
