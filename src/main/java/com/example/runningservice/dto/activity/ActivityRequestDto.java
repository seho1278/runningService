package com.example.runningservice.dto.activity;

import com.example.runningservice.enums.ActivityCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

public class ActivityRequestDto {

    @Getter
    public static class Create {

        private String title;
        private Long regularId;
        private ActivityCategory category;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime startTime;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime endTime;
        private String memo;
        private String location;
    }

    @Getter
    public static class Update {

        private String title;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime startTime;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime endTime;
        private String memo;
        private String location;
    }
}
