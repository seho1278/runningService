package com.example.runningservice.dto;

import com.example.runningservice.enums.Gender;
import lombok.*;

@Getter
@Setter
@Builder
public class ProfileVisibilityRequestDto {
    private String userName;
    private String phoneNumber;
    private Gender gender;
    private String birthYear;
}
