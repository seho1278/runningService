package com.example.runningservice.service;

import com.example.runningservice.dto.crewMember.CrewMemberResponseDto;
import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewApplicantService {

    private final CrewMemberRepository crewMemberRepository;
    private final JoinApplicationRepository joinApplicationRepository;
    private final AESUtil aesUtil;

    @Transactional
    public Page<CrewApplicantResponseDto> getAllJoinApplications(Long crewId,
        GetApplicantsRequestDto request) {

        // 기본 정렬 기준 및 방향 설정 (기본값은 신청일자(appliedAt) 기준 내림차순)
        // 정렬 순서는 조정 가능. 정렬 기준은 신청일자로 고정
        String defaultSortBy = "createdAt";
        int defaultNumber = 0;
        int defaultPage = 10;
        Pageable sortedPageable = PageUtil.getSortedPageable(request.getPageable(), defaultSortBy,
            Direction.ASC, defaultNumber, defaultPage);

        JoinStatus status = request.getStatus();
        Page<JoinApplyEntity> joinApplyEntityPage =
            status == null ? joinApplicationRepository.findAllByCrew_Id(crewId, sortedPageable)
                : joinApplicationRepository.findAllByCrew_IdAndStatus(crewId, status,
                    sortedPageable);

        return joinApplyEntityPage.map(CrewApplicantResponseDto::of);
    }

    @Transactional
    public CrewApplicantDetailResponseDto getJoinApplicationDetail(Long crewId, Long joinApplyId) {
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndCrew_Id(
            joinApplyId, crewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        //runGoalResponseDto 를 조회기능에서 받아옴
        //runRecordResponseDto 를 조회기능에서 받아옴
        return CrewApplicantDetailResponseDto.of(joinApplyEntity);
    }

    @Transactional
    public CrewMemberResponseDto approveJoinApplication(Long joinApplyId) {

        //변경하려는 신청 내역 데이터 가져오기(상태 -> Pending)
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));
        //상태 변경(Pending -> Approved)
        joinApplyEntity.markAsJoinApproved();
        //CrewMemberEntity 생성 후 저장
        MemberEntity memberEntity = joinApplyEntity.getMember();
        CrewEntity crewEntity = joinApplyEntity.getCrew();
        CrewMemberEntity newMember = CrewMemberEntity.of(memberEntity, crewEntity);
        //DTO 변환
        CrewMemberEntity savedCrewMember = crewMemberRepository.save(newMember);
        return CrewMemberResponseDto.of(savedCrewMember, aesUtil);
    }


    @Transactional
    public String rejectJoinApplication(Long joinApplyId) {
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        joinApplyEntity.markAsRejected();

        StringBuilder sb = new StringBuilder();
        sb.append(joinApplyEntity.getMember().getEmail()).append("님의 가입신청이 거부되었습니다.");
        return new String(sb);
    }
}
