package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CrewMemberResponseDto {

    private String memberNickName;
    private String memberProfileImage;

    public static CrewMemberResponseDto of(CrewMemberEntity crewMemberEntity) {
        MemberEntity member = crewMemberEntity.getMember();
        return CrewMemberResponseDto.builder()
            .memberNickName(member.getNickName())
            .memberProfileImage(member.getProfileImageUrl())
            .build();
    }
}
