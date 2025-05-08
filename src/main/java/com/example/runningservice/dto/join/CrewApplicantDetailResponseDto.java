package com.example.runningservice.dto.join;

import com.example.runningservice.dto.runProfile.RunProfile;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.enums.JoinStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CrewApplicantDetailResponseDto extends CrewApplicantSimpleResponseDto {

    private JoinStatus status;
    private RunProfile runProfile;

    public static CrewApplicantDetailResponseDto of(JoinApplyEntity entity) {

        CrewApplicantSimpleResponseDto simpleDto = CrewApplicantSimpleResponseDto.of(entity);

        return CrewApplicantDetailResponseDto.builder()
            .id(simpleDto.getId())
            .nickName(simpleDto.getNickName())
            .profileImage(simpleDto.getProfileImage())
            .message(simpleDto.getMessage())
            .appliedAt(simpleDto.getAppliedAt())
            .status(entity.getStatus())
            .build();
    }

    public void addRunProfile(RunProfile runProfile) {
        this.runProfile = runProfile;
    }


}
