package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.util.S3FileUtil;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CrewMemberResponseDto {

    private String memberNickName;
    private String memberProfileImage;

    public static CrewMemberResponseDto of(CrewMemberEntity crewMemberEntity, S3FileUtil s3FileUtil) {
        MemberEntity member = crewMemberEntity.getMember();
        return CrewMemberResponseDto.builder()
            .memberNickName(member.getNickName())
            .memberProfileImage(s3FileUtil.createPresignedUrl(member.getProfileImageUrl()))
            .build();
    }
}
