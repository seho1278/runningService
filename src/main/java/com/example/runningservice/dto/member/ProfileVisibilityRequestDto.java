package com.example.runningservice.dto.member;

import com.example.runningservice.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVisibilityRequestDto {
    private Visibility userName;
    private Visibility phoneNumber;
    private Visibility gender;
    private Visibility birthYear;
    private Visibility runProfile;
}
