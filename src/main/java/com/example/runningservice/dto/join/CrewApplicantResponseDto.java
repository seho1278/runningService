package com.example.runningservice.dto.join;

import com.example.runningservice.entity.JoinApplyEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CrewApplicantResponseDto {

    private String nickName;
    private String profileImage;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime appliedAt;

    public static CrewApplicantResponseDto of(JoinApplyEntity entity) {
        return CrewApplicantResponseDto.builder()
            .nickName(entity.getMember().getNickName())
            .profileImage(entity.getMember().getProfileImageUrl())
            .message(entity.getMessage())
            .appliedAt(entity.getCreatedAt())
            .build();
    }
}
