package com.example.runningservice.dto.activity;

import com.example.runningservice.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ParticipantResponseDto {

    private Long userId;
    private Long activityId;
    private String nickName;
    private Gender gender;
    private Integer birthYear;
}
