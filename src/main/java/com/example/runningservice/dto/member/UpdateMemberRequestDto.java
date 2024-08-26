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
    private int gender;
    private Integer birthYear;
    private String activityRegion;
    private MultipartFile profileImage;
}
