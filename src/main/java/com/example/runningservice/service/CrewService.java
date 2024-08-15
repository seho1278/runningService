package com.example.runningservice.service;

import com.example.runningservice.dto.crew.CrewRequestDto.Create;
import com.example.runningservice.dto.crew.CrewRequestDto.Update;
import com.example.runningservice.dto.crew.CrewResponseDto.CrewData;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.chat.ChatRoomService;
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
    private final String DEFAULT_IMAGE_NAME = "crew-default";
    private final ChatRoomService chatRoomService;

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
        
        // crew 채팅방 생성
        chatRoomService.createChatRoom(crewEntity.getCrewId(),
            crewEntity.getCrewName(), ChatRoom.CREW);
        // crew staff 채팅방 생성
        chatRoomService.createChatRoom(crewEntity.getCrewId(),
            crewEntity.getCrewName() + "_Staff", ChatRoom.CREW_STAFF);

        return CrewData.fromEntityAndLeaderNameAndOccupancy(
            crewEntity,
            memberEntity.getNickName(),
            1);
    }

    /**
     * s3에 크루 이미지 저장 :: crew-{crewId}로 저장
     */
    private String uploadFileAndReturnFileName(Long crewId, MultipartFile crewImage) {
        if (!crewImage.isEmpty()) {
            String fileName = "crew-" + crewId;
            s3FileUtil.putObject(fileName, crewImage);

            return s3FileUtil.getImgUrl(fileName);
        } else { // 크루 이미지가 없으면 기본 이미지로 사용
            return s3FileUtil.getImgUrl(DEFAULT_IMAGE_NAME);
        }
    }

    /**
     * 크루 정보 수정 :: 크루 db 수정 - 이미지 s3 저장 - 수정된 크루 정보 리턴
     */
    @Transactional
    public CrewData updateCrew(Update updateCrew) {
        CrewEntity crewEntity = crewRepository.findById(updateCrew.getCrewId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        crewEntity.updateFromDto(updateCrew);

        crewEntity.updateCrewImageUrl(uploadFileAndReturnFileName(crewEntity.getCrewId(),
            updateCrew.getCrewImage()));

        return CrewData.fromEntityAndLeaderNameAndOccupancy(
            crewEntity,
            crewEntity.getMember().getNickName(),
            getCrewOccupancy(updateCrew.getCrewId()));
    }

    /**
     * 크루의 현재 크루원 수 조회
     */
    private int getCrewOccupancy(Long crewId) {
        return crewMemberRepository.countByCrew_CrewIdAndStatus(crewId, JoinStatus.APPROVED);
    }

    /**
     * 크루 삭제 :: 크루 이미지 삭제 - 크루원 삭제 - 크루 삭제
     */
    @Transactional
    public CrewData deleteCrew(Long crewId) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        // 삭제하기 전에 리턴하기 위한 데이터를 미리 저장해둔다.
        CrewData crewData = CrewData.fromEntityAndLeaderNameAndOccupancy(
            crewEntity,
            crewEntity.getMember().getNickName(),
            getCrewOccupancy(crewId));

        // 이미지가 디폴트가 아닌 경우에만 삭제
        String fileName = findLastPath(crewEntity.getCrewImage());
        if (!fileName.equals(DEFAULT_IMAGE_NAME)) {
            s3FileUtil.deleteObject(fileName);
        }

        // 크루원 삭제
        crewMemberRepository.deleteAllByCrew_CrewId(crewId);

        crewRepository.delete(crewEntity);

        return crewData;
    }

    /**
     * url의 가장 끝 path를 리턴한다.
     */
    private String findLastPath(String url) {
        String[] paths = url.split("/");

        return paths[paths.length - 1];
    }
}
