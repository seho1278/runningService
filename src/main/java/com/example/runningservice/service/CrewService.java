package com.example.runningservice.service;

import com.example.runningservice.dto.crew.CrewBaseResponseDto;
import com.example.runningservice.dto.crew.CrewCreateRequestDto;
import com.example.runningservice.dto.crew.CrewDetailResponseDto;
import com.example.runningservice.dto.crew.CrewFilterDto;
import com.example.runningservice.dto.crew.CrewJoinStatusResponseDto;
import com.example.runningservice.dto.crew.CrewRoleResponseDto;
import com.example.runningservice.dto.crew.CrewUpdateRequestDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.ChatRoom;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.OccupancyStatus;
import com.example.runningservice.enums.Region;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.repository.crewMember.CrewMemberBlackListRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import com.example.runningservice.service.chat.ChatRoomService;
import com.example.runningservice.util.S3FileUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final ActivityRepository activityRepository;
    private final S3FileUtil s3FileUtil;
    private final String DEFAULT_IMAGE_NAME = "crew-default";
    private final ChatRoomService chatRoomService;
    private final RegularRunMeetingRepository regularRunMeetingRepository;
    private final CrewMemberBlackListRepository crewMemberBlackListRepository;
    private final JoinApplicationRepository joinApplicationRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 크루 생성 :: db에 크루 저장 - 이미지 s3 저장 - 생성한 크루 정보 리턴
     */
    @Transactional
    public CrewBaseResponseDto createCrew(CrewCreateRequestDto newCrew, Long loginId) {
        MemberEntity memberEntity = memberRepository.findById(loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        CrewEntity crewEntity = CrewEntity.toEntity(newCrew, memberEntity);
        crewRepository.save(crewEntity);

        crewEntity.updateCrewImageUrl(uploadFileAndReturnFileName(crewEntity.getId(),
            newCrew.getCrewImage()));

        crewMemberRepository.save(CrewMemberEntity.builder()
            .crew(crewEntity)
            .member(memberEntity)
            .role(CrewRole.LEADER)
            .build());

        // crew 채팅방 생성
        chatRoomService.createChatRoom(crewEntity.getId(),
            crewEntity.getCrewName(), ChatRoom.CREW);
        // crew staff 채팅방 생성
        chatRoomService.createChatRoom(crewEntity.getId(),
            crewEntity.getCrewName(), ChatRoom.CREW_STAFF);

        return CrewBaseResponseDto.fromEntity(crewEntity, s3FileUtil);
    }

    /**
     * s3에 크루 이미지 저장 :: crew-{crewId}로 저장
     */
    private String uploadFileAndReturnFileName(Long crewId, MultipartFile crewImage) {
        if (crewImage != null && !crewImage.isEmpty()) {
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
    public CrewBaseResponseDto updateCrew(CrewUpdateRequestDto updateCrew, Long crewId) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        crewEntity.updateFromDto(updateCrew);

        if (Boolean.TRUE.equals(updateCrew.getDeleteCrewImage())) {
            s3FileUtil.deleteObject("crew-" + crewId);
            crewEntity.updateCrewImageUrl(
                uploadFileAndReturnFileName(crewEntity.getId(), updateCrew.getCrewImage()));
        } else if (updateCrew.getCrewImage() != null) {
            crewEntity.updateCrewImageUrl(uploadFileAndReturnFileName(crewEntity.getId(),
                updateCrew.getCrewImage()));
        }

        return CrewBaseResponseDto.fromEntity(crewEntity, s3FileUtil);
    }

    /**
     * 크루 삭제 :: 크루 이미지 삭제 - 크루원 삭제 - 크루 삭제
     */
    @Transactional
    public CrewBaseResponseDto deleteCrew(Long crewId) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        // 삭제하기 전에 리턴하기 위한 데이터를 미리 저장해둔다.
        CrewBaseResponseDto crewData = CrewBaseResponseDto.fromEntity(crewEntity, s3FileUtil);

        // 이미지가 디폴트가 아닌 경우에만 삭제
        String fileName = findLastPath(crewEntity.getCrewImage());
        if (!fileName.equals(DEFAULT_IMAGE_NAME)) {
            s3FileUtil.deleteObject(fileName);
        }

        // 외래키로 사용중인 테이블에서 모두 삭제 (크루원, 정기러닝 정보, 활동, 크루블랙리스트, 가입 신청, 채팅방)
        crewMemberRepository.deleteAllByCrew_Id(crewId);
        regularRunMeetingRepository.deleteByCrew_Id(crewId);
        activityRepository.deleteByCrew_Id(crewId);
        crewMemberBlackListRepository.deleteByCrew_Id(crewId);
        joinApplicationRepository.deleteByCrew_Id(crewId);
        chatRoomRepository.deleteByCrew_Id(crewId);

        crewRepository.delete(crewEntity);

        return crewData;
    }

    /**
     * 크루 싱세 정보 조회
     */
    public CrewDetailResponseDto getCrew(Long loginId, Long crewId) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        return CrewDetailResponseDto.fromEntity(crewEntity,
            checkJoinedCrew(crewEntity.getCrewMember(), loginId), activityRepository, s3FileUtil);
    }

    /**
     * 참가 중인 크루 리스트 조회
     */
    public Page<CrewRoleResponseDto> getParticipateCrewList(Long loginId, Pageable pageable) {
        Page<CrewMemberEntity> crewMemberEntities = crewMemberRepository
            .findByMember_IdOrderByJoinedAt(loginId, pageable);

        return crewMemberEntities.map(entity -> CrewRoleResponseDto.fromEntity(entity, s3FileUtil));
    }

    /**
     * 전체 크루 필터링 조회
     */
    public Page<CrewJoinStatusResponseDto> getCrewList(Long loginId,
        CrewFilterDto.CrewInfo crewFilter, Pageable pageable) {

        // 로그인을 했고 따로 입력한 지역 정보가 없으면 회원이 설정한 지역 기반으로 조회한다.
        if (crewFilter.getActivityRegion() == null && loginId != null) {
            crewFilter.updateRegionForLoginUser(getRegionForLoginUser(loginId));
        }

        // 전국 조회 시 모든 지역 조회가 가능하므로 필터값을 없애줌
        if (Region.NATIONWIDE.equals(crewFilter.getActivityRegion())) {
            crewFilter.updateRegionForLoginUser(null);
        }

        Page<CrewEntity> crewEntityList = (crewFilter.getOccupancyStatus() != null) ?
            crewFilter.getOccupancyStatus().getCrewList(crewRepository, crewFilter, pageable) :
            OccupancyStatus.ALL.getCrewList(crewRepository, crewFilter, pageable);

        return crewEntityList.map(entity -> CrewJoinStatusResponseDto.fromEntity(entity,
            checkJoinedCrew(entity.getCrewMember(), loginId), s3FileUtil));
    }

    /**
     * url의 가장 끝 path를 리턴한다.
     */
    private String findLastPath(String url) {
        String[] paths = url.split("/");

        return paths[paths.length - 1];
    }

    // 사용자의 활동 지역 조회
    private Region getRegionForLoginUser(Long loginId) {
        MemberEntity memberEntity = memberRepository.findMemberById(loginId);

        return memberEntity.getActivityRegion();
    }

    // 조회한 크루가 가입한 크루인지 확인한다.
    private boolean checkJoinedCrew(List<CrewMemberEntity> crewMemberList, Long userId) {
        if (userId == null) {
            return false;
        }

        return crewMemberList.stream()
            .anyMatch(crewMemberEntity -> crewMemberEntity.getMember().getId().equals(userId));
    }
}
