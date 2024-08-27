package com.example.runningservice.dto.member;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}
