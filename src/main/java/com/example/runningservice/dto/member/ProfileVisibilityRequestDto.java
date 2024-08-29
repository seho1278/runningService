package com.example.runningservice.dto.member;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Visibility;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVisibilityRequestDto {
    private Visibility userName;
    private Visibility phoneNumber;
    private Visibility gender;
    private Visibility birthYear;
}
