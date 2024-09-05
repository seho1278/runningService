package com.example.runningservice.dto.member;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRequestDto {
    private String nickName;
    private Gender gender;
    private Integer birthYear;
    private Region activityRegion;
    private MultipartFile profileImage;

    private Visibility nameVisibility;
    private Visibility phoneNumberVisibility;
    private Visibility genderVisibility;
    private Visibility birthYearVisibility;
    private Visibility runProfileVisibility;
}
