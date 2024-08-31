package com.example.runningservice.service;

import com.example.runningservice.dto.crewMember.ChangeCrewRoleRequestDto;
import com.example.runningservice.dto.crewMember.ChangedLeaderResponseDto;
import com.example.runningservice.dto.crewMember.GetCrewMemberRequestDto;
import com.example.runningservice.entity.CrewMemberBlackListEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.repository.crewMember.CrewMemberBlackListRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;
    private final JoinApplicationRepository joinApplicationRepository;
    private final CrewMemberBlackListRepository crewMemberBlackListRepository;
    private final ChatJoinRepository chatJoinRepository;

    /**
     * 크루원 조회
     */
    @Transactional
    public Page<CrewMemberEntity> getCrewMembers(Long crewId,
        GetCrewMemberRequestDto.Filter filterDto,
        Pageable pageable) {

        //정렬기준의 유효성 검증
        pageable.getSort().stream()
            .forEach(order -> {
                if (!isValidSortField(order.getProperty())) {
                    throw new CustomException(ErrorCode.INVALID_SORT);
                }
            });

        if (filterDto.getCrewRole() == null
            && filterDto.getGender() == null && filterDto.getMaxYear() == null
            && filterDto.getMinYear() == null) {
            return crewMemberRepository.findByCrew_IdOrderByRoleOrderAsc(crewId,
                pageable);
        }

        return crewMemberRepository.findAllByCrewIdAndFilter(crewId, filterDto, pageable);
    }

    /**
     * 크루원 개별조회(상세조회)
     */
    @Transactional
    public CrewMemberEntity getCrewMember(Long crewMemberId) {
        return crewMemberRepository.findById(crewMemberId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW_MEMBER));
    }

    /**
     * 크루 탈퇴(일반 탈퇴)
     */
    @Transactional
    public void leaveCrew(Long crewId, Long userId) {
        //crewId와 userId로 요청자 데이터 찾기
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByCrew_IdAndMember_Id(
            crewId, userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW_MEMBER));

        //퇴장 후 신청자 테이블에서 상태를 WITHDRAWN 으로 변경
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(
            userId, crewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        joinApplyEntity.markAsWithdrawn();

        //크루멤버 테이블에서 지우기
        crewMemberRepository.delete(crewMemberEntity);

        //가입해있던 크루 내 모든 채팅방에서 퇴장(삭제)
        chatJoinRepository.deleteAllByMemberIdAndCrewId(crewMemberEntity.getMember().getId(),
            crewId);

        StringBuilder sb = new StringBuilder();
        sb.append(joinApplyEntity.getMember().getEmail())
            .append(" 님이 ")
            .append(joinApplyEntity.getCrew().getCrewName())
            .append(" 크루를 탈퇴하셨습니다.");
        log.info(new String(sb));
    }

    /**
     * 강제퇴장
     */
    @Transactional
    public CrewMemberBlackListEntity removeCrewMember(Long crewId, Long crewMemberId) {
        //퇴장시킬 크루원 데이터 찾기
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findById(crewMemberId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW_MEMBER));

        //퇴장 후 신청자 테이블에성 상태를 FORCE_WITHDRAWN 으로 변경
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(
                crewMemberEntity.getMember().getId(), crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        joinApplyEntity.markAsForceWithdrawn();

        //크루멤버 테이블에서 지우기
        crewMemberRepository.delete(crewMemberEntity);

        //채팅방에서도 퇴장(해당 유저가 가입한 크루 내 모든 채팅방에서 퇴장)
        chatJoinRepository.deleteAllByMemberIdAndCrewId(crewMemberEntity.getMember().getId(),
            crewId);

        //블랙리스트에 멤버 추가
        return crewMemberBlackListRepository.save(CrewMemberBlackListEntity.builder()
            .member(crewMemberEntity.getMember())
            .crew(crewMemberEntity.getCrew())
            .build());
    }

    /**
     * 크루원 권한 변경
     */
    @Transactional
    public CrewMemberEntity changeRole(ChangeCrewRoleRequestDto requestDto) {
        //크루원 데이터 가져오기
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findById(
                requestDto.getCrewMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW_MEMBER));

        //크루원 권한 변경
        crewMemberEntity.changeRoleTo(requestDto.getNewRole());

        return crewMemberEntity;
    }

    /**
     * 리더 권한 위임
     */
    @Transactional
    public ChangedLeaderResponseDto transferLeaderRole(Long userId, Long crewId,
        Long crewMemberId) {
        CrewMemberEntity newLeader = crewMemberRepository.findById(crewMemberId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW_MEMBER));

        CrewMemberEntity oldLeader = crewMemberRepository.findByMember_IdAndCrew_Id(
            userId, crewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW_MEMBER));

        newLeader.acceptLeaderRole();

        oldLeader.changeRoleTo(CrewRole.MEMBER);

        return ChangedLeaderResponseDto.builder()
            .oldLeaderNickName(oldLeader.getMember().getNickName())
            .oldLeaderRole(oldLeader.getRole())
            .newLeaderNickName(newLeader.getMember().getNickName())
            .newLeaderRole(newLeader.getRole())
            .build();
    }

    private final List<String> ALLOWED_SORT_FIELDS = List.of(
        "joinedAt", "member.nickName" // 허용된 필드들
    );

    private boolean isValidSortField(String field) {
        return ALLOWED_SORT_FIELDS.contains(field);
    }
}
