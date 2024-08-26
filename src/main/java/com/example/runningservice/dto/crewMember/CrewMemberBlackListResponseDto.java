package com.example.runningservice.dto.crewMember;

import com.example.runningservice.entity.CrewMemberBlackListEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrewMemberBlackListResponseDto {
    private Long blackListId;
    private String memberNickName;
    private String memberEmail;
    private String crewName;
    private LocalDateTime bannedAt;

    public static CrewMemberBlackListResponseDto of(CrewMemberBlackListEntity crewMemberBlackListEntity) {
        return CrewMemberBlackListResponseDto.builder()
            .blackListId(crewMemberBlackListEntity.getId())
            .memberNickName(crewMemberBlackListEntity.getMember().getNickName())
            .memberEmail(crewMemberBlackListEntity.getMember().getEmail())
            .crewName(crewMemberBlackListEntity.getCrew().getCrewName())
            .bannedAt(crewMemberBlackListEntity.getCreatedAt())
            .build();
    }
}
