package com.example.runningservice.dto.join;

import com.example.runningservice.entity.JoinApplyEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CrewApplicantSimpleResponseDto {

    private Long id;
    private String nickName;
    private String profileImage;
    private String message;
    private LocalDateTime appliedAt;

    public static CrewApplicantSimpleResponseDto of(JoinApplyEntity entity) {
        return CrewApplicantSimpleResponseDto.builder()
            .id(entity.getId())
            .nickName(entity.getMember().getNickName())
            .profileImage(entity.getMember().getProfileImageUrl())
            .message(entity.getMessage())
            .appliedAt(entity.getCreatedAt())
            .build();
    }
}
