package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.util.AESUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CrewMemberResponseDto {

    private String memberNickName;
    private String name;
    private String phoneNumber;
    private Integer birthYear;
    private Gender memberGender;
    private Region memberActivityRegion;
    private String memberProfileImage;
    private String crewName;
    private CrewRole role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime joinedAt;

    public static CrewMemberResponseDto of(CrewMemberEntity crewMemberEntity, AESUtil aesUtil) {
        MemberEntity member = crewMemberEntity.getMember();
        // 기본적으로 모든 필드를 공개하지 않도록 설정
        CrewMemberResponseDto.CrewMemberResponseDtoBuilder dtoBuilder = CrewMemberResponseDto.builder()
            .memberNickName(member.getNickName())
            .crewName(crewMemberEntity.getCrew().getCrewName())
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

        if (member.getActivityRegion() != null) {
            dtoBuilder.memberActivityRegion(member.getActivityRegion());
        }

        if (member.getProfileImageUrl() != null) {
            dtoBuilder.memberProfileImage(member.getProfileImageUrl());
        }

        return dtoBuilder.build();
    }
}
