package com.example.runningservice.service;

import com.example.runningservice.dto.crew.CrewRequestDto.Create;
import com.example.runningservice.dto.crew.CrewResponseDto.CrewData;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final S3FileUtil s3FileUtil;

    /**
     * 크루 생성 :: db에 크루 저장 - 이미지 s3 저장 - 생성한 크루 정보 리턴
     */
    @Transactional
    public CrewData createCrew(Create newCrew) {
        MemberEntity memberEntity = memberRepository.findById(newCrew.getLeaderId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        CrewEntity crewEntity = CrewEntity.toEntity(newCrew, memberEntity);
        crewRepository.save(crewEntity);

        crewEntity.updateCrewImageUrl(uploadFileAndReturnFileName(crewEntity.getCrewId(),
            newCrew.getCrewImage()));

        crewMemberRepository.save(CrewMemberEntity.builder()
            .crew(crewEntity)
            .member(memberEntity)
            .role(CrewRole.LEADER)
            .status(JoinStatus.APPROVED)
            .build());

        return CrewData.fromEntityAndLeaderNameAndOccupancy(
            crewEntity,
            memberEntity.getNickName(),
            1);
    }

    /**
     * s3에 크루 이미지 저장 :: crew-{crewId}로 저장
     */
    private String uploadFileAndReturnFileName(Long crewId, MultipartFile crewImage) {
        if (crewImage != null) {
            String fileName = "crew-" + crewId;
            s3FileUtil.putObject(fileName, crewImage);

            return s3FileUtil.getImgUrl(fileName);
        } else { // 크루 이미지가 없으면 기본 이미지로 사용
            return s3FileUtil.getImgUrl("crew-default");
        }
    }

    /**
     * 크루의 현재 크루원 수 조회
     */
    private int getCrewOccupancy(Long crewId) {
        return crewMemberRepository.countByCrew_CrewIdAndStatus(crewId, JoinStatus.APPROVED);
    }
}
