package com.example.runningservice.dto.crewMember;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.util.validator.YearRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetCrewMemberRequestDto {

    @Getter
    @Builder
    @YearRange(minYear = "minYear", maxYear = "maxYear")
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Filter {
        private Gender gender;
        private Integer minYear;
        private Integer maxYear;
        private CrewRole crewRole;
    }
}
