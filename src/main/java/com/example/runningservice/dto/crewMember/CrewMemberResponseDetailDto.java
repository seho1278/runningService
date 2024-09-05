package com.example.runningservice.dto.crewMember;

import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.util.AESUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CrewMemberResponseDetailDto extends CrewMemberResponseDto {

    private String name;
    private String phoneNumber;
    private Integer birthYear;
    private Gender memberGender;
    private Region memberActivityRegion;
    private CrewRole role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime joinedAt;
    private RunProfile runProfile;

    @Builder
    @Getter
    @Setter
    private static class RunProfile {
        private Double totalDistanceGoal;
        private Integer totalRunningTimeGoal;
        private Integer averagePaceGoal;
        private Integer runCountGoal;

        private Double totalDistance;
        private Integer totalRunningTime;
        private Integer averagePace;
        private Integer runCount;
    }

    public static CrewMemberResponseDetailDto of(CrewMemberEntity crewMemberEntity,
        AESUtil aesUtil) {
        MemberEntity member = crewMemberEntity.getMember();
        // 기본적으로 모든 필드를 공개하지 않도록 설정
        CrewMemberResponseDetailDtoBuilder dtoBuilder = CrewMemberResponseDetailDto.builder()
            .memberNickName(member.getNickName())
            .memberProfileImage(member.getProfileImageUrl())
            .memberActivityRegion(member.getActivityRegion())
            .role(crewMemberEntity.getRole())
            .joinedAt(crewMemberEntity.getJoinedAt());

        // 공개 설정에 따라 필드를 추가 (이름, 연령, 성별, 전화번호, 활동지역, 프로필 이미지)
        if (member.getNameVisibility() == Visibility.PUBLIC) {
            dtoBuilder.name(member.getName());
        }

        if (member.getBirthYearVisibility() == Visibility.PUBLIC) {
            dtoBuilder.birthYear(member.getBirthYear());
        }

        if (member.getGenderVisibility() == Visibility.PUBLIC) {
            dtoBuilder.memberGender(member.getGender());
        }

        if (member.getPhoneNumberVisibility() == Visibility.PUBLIC) {
            dtoBuilder.phoneNumber(aesUtil.decrypt(member.getPhoneNumber()));
        }

        return dtoBuilder.build();
    }

    public void addRunProfile(RunGoalEntity runGoal,
        RunRecordResponseDto runRecord) {

        this.runProfile = RunProfile.builder()
            .totalDistanceGoal(runGoal.getTotalDistance())
            .runCountGoal(runGoal.getRunCount())
            .averagePaceGoal(runGoal.getAveragePace())
            .totalRunningTimeGoal(runGoal.getTotalRunningTime())
            .totalDistance(runRecord.getDistance())
            .totalRunningTime(runRecord.getRunningTime())
            .averagePace(runRecord.getPace())
            .runCount(runRecord.getRunCount())
            .build();
    }
}
