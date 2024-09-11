package com.example.runningservice.service;

import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantSimpleResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewApplicantService {

    private final CrewMemberRepository crewMemberRepository;
    private final JoinApplicationRepository joinApplicationRepository;
    private final ProfileWithRunService profileWithRunService;

    @Transactional
    public Page<CrewApplicantSimpleResponseDto> getAllJoinApplications(Long crewId,
        GetApplicantsRequestDto request) {

        // 정렬 순서는 조정 가능. 정렬 기준은 신청일자로 고정
        Pageable sortedPageable = request.getPageable();

        JoinStatus status = request.getStatus();
        Page<JoinApplyEntity> joinApplyEntityPage =
            status == null ? joinApplicationRepository.findAllByCrew_Id(crewId, sortedPageable)
                : joinApplicationRepository.findAllByCrew_IdAndStatus(crewId, status,
                    sortedPageable);

        return joinApplyEntityPage.map(CrewApplicantSimpleResponseDto::of);
    }

    @Transactional
    public CrewApplicantDetailResponseDto getJoinApplicationDetail(Long crewId, Long joinApplyId) {
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndCrew_Id(
            joinApplyId, crewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        return profileWithRunService.getJoinApplicationDetail(joinApplyEntity);
    }

    @Transactional
    public CrewMemberEntity approveJoinApplication(Long joinApplyId) {

        //변경하려는 신청 내역 데이터 가져오기(상태 -> Pending)
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));
        //상태 변경(Pending -> Approved)
        joinApplyEntity.markAsJoinApproved();
        //CrewMemberEntity 생성 후 저장
        CrewMemberEntity newMember = CrewMemberEntity.of(joinApplyEntity.getMember(),
            joinApplyEntity.getCrew());
        //DTO 변환
        return crewMemberRepository.save(newMember);
    }


    @Transactional
    public JoinApplyEntity rejectJoinApplication(Long joinApplyId) {
        // 대기 상태인 신청 데이터 가져오기
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));
        // 신청 상태를 "REJECTED"로 수정
        joinApplyEntity.markAsRejected();

        return joinApplyEntity;
    }
}
