package com.example.runningservice.dto;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequestDto {
    private String nickName;
    private Gender gender;
    private Integer birthYear;
    private Region activityRegion;
}
