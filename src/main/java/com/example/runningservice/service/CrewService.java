package com.example.runningservice.service;

import com.example.runningservice.dto.crew.CrewFilterDto;
import com.example.runningservice.dto.crew.CrewRequestDto.Create;
import com.example.runningservice.dto.crew.CrewRequestDto.Update;
import com.example.runningservice.dto.crew.CrewResponseDto.CrewData;
import com.example.runningservice.dto.crew.CrewResponseDto.Detail;
import com.example.runningservice.dto.crew.CrewResponseDto.Summary;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.OccupancyStatus;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.chat.ChatRoomService;
import com.example.runningservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            crewEntity.getCrewName(), ChatRoom.CREW_STAFF);

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
            crewEntity.getLeader().getNickName(),
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
            crewEntity.getLeader().getNickName(),
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

    /**
     * 크루 싱세 정보 조회
     */
    public Detail getCrew(Long crewId) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        Detail detail = Detail.fromEntity(crewEntity);
        detail.setLeaderName(crewEntity.getLeader().getNickName());
        detail.setCrewOccupancy(getCrewOccupancy(crewId));
        detail.setRunningCount(0); // TODO: 활동 기능 추가되면 추가

        return detail;
    }

    /**
     * 참가 중인 크루 리스트 조회 (내가 만든 크루, 가입한 크루로 필터링하여 조회 가능)
     */
    public Summary getParticipateCrewList(CrewFilterDto.Participate participate,
        Pageable pageable) {
        Pageable customPageable = PageRequest.of(pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Order.desc("joinedAt")));
        Summary summaryCrew = new Summary();
        Page<CrewMemberEntity> crewMemberEntities;

        if (participate.getFilter() != null) {
            crewMemberEntities = crewMemberRepository.findByMember_IdAndRole(
                participate.getUserId(), participate.getFilter().getCrewRole(), customPageable);
        } else { // filter가 없으면 내가 만든/가입한 크루 전체 조회
            crewMemberEntities = crewMemberRepository.findByMember_Id(
                participate.getUserId(), customPageable);
        }

        crewMemberEntities.getContent()
            .forEach(x -> summaryCrew.addCrew(CrewData.fromEntityAndLeaderNameAndOccupancy(
                x.getCrew(), x.getMember().getNickName(), getCrewOccupancy(x.getId()))));

        return summaryCrew;
    }

    /**
     * 전체 크루 필터링 조회
     */
    public Summary getCrewList(CrewFilterDto.CrewInfo crewFilter, Pageable pageable) {
        Page<Object[]> crewList = (crewFilter.getOccupancyStatus() != null) ?
            crewFilter.getOccupancyStatus().getCrewList(crewRepository, crewFilter, pageable) :
            OccupancyStatus.ALL.getCrewList(crewRepository, crewFilter, pageable);

        Summary summary = new Summary();
        for (Object[] object : crewList) {
            CrewEntity crewEntity = (CrewEntity) object[0];
            String leaderNickname = (String) object[1];
            int occupancy = ((Long) object[2]).intValue();

            summary.addCrew(CrewData.fromEntityAndLeaderNameAndOccupancy(
                crewEntity,
                leaderNickname,
                occupancy));
        }

        return summary;
    }
}
