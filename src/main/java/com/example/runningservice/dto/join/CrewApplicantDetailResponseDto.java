package com.example.runningservice.dto.join;

import com.example.runningservice.entity.JoinApplyEntity;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class CrewApplicantDetailResponseDto extends CrewApplicantResponseDto {

    //RunRecordDto, RunGoalDto 추가

    public static CrewApplicantDetailResponseDto of(JoinApplyEntity entity) {

        CrewApplicantDetailResponseDto crewApplicantDetailResponseDto = CrewApplicantDetailResponseDto.builder()
            .nickName(entity.getMember().getNickName())
            .profileImage(entity.getMember().getProfileImageUrl())
            .message(entity.getMessage())
            .appliedAt(entity.getCreatedAt())
            .build();
        //setRunRecordDto(runRecordDto, runGoalDto)
        return crewApplicantDetailResponseDto;
    }

    //private void setRunRecordDto(RunRecordDto runRecordDto, RunGoalDto runGoalDto) {
    //      this.runRecordDto = runRecordDto;
    //      this.runGoalDto = runGoalDto;
    // }
}
