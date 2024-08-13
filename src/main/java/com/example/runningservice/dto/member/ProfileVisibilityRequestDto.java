package com.example.runningservice.dto.member;

import com.example.runningservice.enums.Gender;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVisibilityRequestDto {
    private String userName;
    private String phoneNumber;
    private Gender gender;
    private String birthYear;
}
