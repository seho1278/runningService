package com.example.runningservice.dto.member;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequestDto {
    private String nickName;
    private Gender gender;
    private Integer birthYear;
    private Region activityRegion;
    private MultipartFile profileImage;
}
