package com.example.runningservice.dto;

import com.example.runningservice.enums.Gender;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVisibilityRequestDto {
    private int userName;
    private int phoneNumber;
    private int gender;
    private int birthYear;
}
