package com.example.runningservice.dto.regular_run;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CrewRegularRunResponseDto {

    private Long crewId;
    private List<RegularRunResponseDto> data;
}
