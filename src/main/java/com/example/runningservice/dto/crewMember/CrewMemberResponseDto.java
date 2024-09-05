package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.util.S3FileUtil;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CrewMemberResponseDto {

    private Long crewMemberId;
    private String memberNickName;
    private String memberProfileImage;

    public static CrewMemberResponseDto of(CrewMemberEntity crewMemberEntity, S3FileUtil s3FileUtil) {
        MemberEntity member = crewMemberEntity.getMember();
        return CrewMemberResponseDto.builder()
            .crewMemberId(crewMemberEntity.getId())
            .memberNickName(member.getNickName())
            .memberProfileImage(s3FileUtil.createPresignedUrl(member.getProfileImageUrl()))
            .build();
    }
}
