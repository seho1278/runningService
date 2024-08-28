package com.example.runningservice.dto.member;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVisibilityResponseDto {
    private Visibility userName;
    private Visibility phoneNumber;
    private Visibility gender;
    private Visibility birthYear;

    public static ProfileVisibilityResponseDto of(MemberEntity memberEntity) {
        return ProfileVisibilityResponseDto.builder()
            .userName(memberEntity.getNameVisibility())
            .phoneNumber(memberEntity.getPhoneNumberVisibility())
            .gender(memberEntity.getGenderVisibility())
            .birthYear(memberEntity.getBirthYearVisibility())
            .build();
    }
}
