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
public class RegularRunResponseDto {

    private Long id;
    private Frequency frequency;
    private List<String> weekdays;
    private String region;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Frequency {

        private int weeks;
        private int times;
    }
}
