package com.example.runningservice.service;

import com.example.runningservice.dto.UpdateJoinApplyDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.dto.join.JoinApplyDto;
import com.example.runningservice.dto.join.JoinApplyDto.SimpleResponse;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.util.JwtUtil;
import com.example.runningservice.util.PageUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserJoinService {

    private final JoinApplicationRepository joinApplicationRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public JoinApplyDto.DetailResponse saveJoinApply(Long crewId,
        JoinApplyDto.Request joinApplyForm) {
        MemberEntity memberEntity = memberRepository.findById(joinApplyForm.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        //크루원 수가 크루 정원보다 작은 크루일 때만 가져오기
        CrewEntity crewEntity = crewRepository.findByIdAndMemberCountLessThanCapacity(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        //멤버제한 조건 검증 : 성별(선택), 최소연령(선택), 최대연령(선택), 러닝기록 오픈여부
        isJoinPossible(memberEntity, crewEntity);

        //crew의 가입승인 필수여부 확인 & 가입승인 필수 아닐 시, 자동으로 가입
        JoinApplyEntity joinApplyEntity = JoinApplyEntity.of(memberEntity, crewEntity,
            joinApplyForm.getMessage());

        if (!crewEntity.getLeaderRequired()) { // 가입 승인이 필요 없는 경우
            // 가입 상태를 승인으로 설정
            joinApplyEntity.markAsJoinApproved();

            // 크루원으로 자동 가입 처리
            CrewMemberEntity crewMemberEntity = CrewMemberEntity.of(memberEntity, crewEntity);
            crewMemberRepository.save(crewMemberEntity);
        } else {
            // 가입 승인이 필요한 경우
            joinApplyEntity.initializeStatusAsPending();
        }
        // 엔티티 저장
        JoinApplyEntity savedJoinApplyEntity = joinApplicationRepository.save(joinApplyEntity);

        return JoinApplyDto.DetailResponse.from(savedJoinApplyEntity);
    }

    //가입신청내역 조회하기(페이지네이션, 신청일자 기준 정렬기준, 신청상태 필터링 적용 추가)
    @Transactional(readOnly = true)
    public Page<SimpleResponse> getJoinApplications(String token, Long memberId,
        GetApplicantsRequestDto request) {

        //토큰 유효성 검사
        token = token.substring("Bearer ".length());
        if (!jwtUtil.validateToken(memberId, token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS);
        }

        // 기본 정렬 기준 및 방향 설정 (기본값은 createdAt 기준 내림차순)
        String defaultSortBy = "createdAt";
        Direction defaultDirection = Direction.ASC;
        Pageable pageable = request.getPageable();
        int defaultPageNumber =  0;
        int defaultSize = 10;

        // 정렬 순서 설정
        Pageable sortedPageable = PageUtil.getSortedPageable(pageable, defaultSortBy, defaultDirection,
            defaultPageNumber, defaultSize);

        // 신청결과 필터링
        JoinStatus status = request.getStatus();
        Page<JoinApplyEntity> joinApplications =
            status == null ? joinApplicationRepository.findAllByMember_Id(memberId, sortedPageable)
                : joinApplicationRepository.findAllByMember_IdAndStatus(memberId, status,
                    sortedPageable);

        // 조회된 데이터를 SimpleResponse로 변환하여 반환
        return joinApplications.map(SimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public JoinApplyDto.DetailResponse getJoinApplicationDetail(String token, Long userId,
        Long joinApplyId) {
        token = token.substring("Bearer ".length());
        if (!jwtUtil.validateToken(userId, token)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // JoinApplyEntity 조회 시, joinRequestId가 잘못된 경우 (존재하지 않는 경우)
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndMember_Id(
            joinApplyId, userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        return JoinApplyDto.DetailResponse.from(joinApplyEntity);
    }

    private void isJoinPossible(MemberEntity memberEntity, CrewEntity crewEntity) {

        Gender requiredGender = crewEntity.getGender();
        Integer minAge = crewEntity.getMinAge();
        Integer maxAge = crewEntity.getMaxAge();
        Long memberId = memberEntity.getId();
        Long crewId = crewEntity.getId();
        // 나이 제한 있으면 검증
        if (minAge != null || maxAge != null) {
            //나이 검증
            validateAge(memberEntity, crewEntity, minAge, maxAge);
        }
        // 성별 제한 있으면 검증
        if (requiredGender != null) {
            // 성별 검증
            validateGender(memberEntity, crewEntity, requiredGender);
        }
        // Todo 기록 공개 여부 검증

        //이미 회원이면 신청 불가
        if (crewMemberRepository.existsByMember_Id(memberId)) {
            throw new CustomException(ErrorCode.ALREADY_CREWMEMBER);
        }
        //최근 신청내역이 대기상태이거나 강제퇴장된 상태면 신청불가
        joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(memberId, crewId)
            .ifPresent(application -> {
                if (application.getStatus() == JoinStatus.PENDING) {
                    throw new CustomException(ErrorCode.ALREADY_EXIST_PENDING_JOIN_APPLY);
                }
                if (application.getStatus() == JoinStatus.FORCE_WITHDRAWN) {
                    throw new CustomException(ErrorCode.JOIN_NOT_ALLOWED_FOR_FORCE_WITHDRAWN);
                }
            });
    }

    private void validateAge(MemberEntity memberEntity, CrewEntity crewEntity, Integer minAge,
        Integer maxAge) {

        if (minAge != null || maxAge != null) {
            if (memberEntity.getBirthYear() == null) {
                throw new CustomException(ErrorCode.AGE_REQUIRED);
            }

            int memberAge = LocalDate.now().getYear() - memberEntity.getBirthYear() + 1; //한국나이
            if (minAge != null && memberAge < minAge) {
                throw new CustomException(ErrorCode.AGE_RESTRICTION_NOT_MET);
            }

            if (maxAge != null && memberAge > maxAge) {
                throw new CustomException(ErrorCode.AGE_RESTRICTION_NOT_MET);
            }
        }
    }

    private void validateGender(MemberEntity memberEntity, CrewEntity crewEntity,
        Gender requiredGender) {
        Gender memberGender = memberEntity.getGender();
        Visibility memberGenderVisibility = memberEntity.getGenderVisibility();

        if (requiredGender != null) {
            if (memberGender == null) {
                throw new CustomException(ErrorCode.GENDER_REQUIRED);
            }
            if (!memberGender.equals(requiredGender)) {
                throw new CustomException(ErrorCode.GENDER_RESTRICTION_NOT_MET);
            }
        }
    }

    @Transactional
    public JoinApplyDto.DetailResponse updateJoinApply(String token,
        UpdateJoinApplyDto updateJoinApplyDto) {
        token = token.substring("Bearer ".length());
        //JoinEntity 가져오기 (대기상태인 것만 가져오기)
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndStatus(
                updateJoinApplyDto.getJoinApplyId(), JoinStatus.PENDING)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        //토큰 주인과 엔티티 작성자 일치여부 확인
        if (!joinApplyEntity.getMember().getId().equals(jwtUtil.extractUserId(token))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS);
        }

        //메세지내용 수정
        joinApplyEntity.updateMessage(updateJoinApplyDto.getMessage());
        return JoinApplyDto.DetailResponse.from(joinApplyEntity);
    }

    //가입신청을 취소하면 JoinApplyRespository에서 삭제
    @Transactional
    public void removeJoinApply(String token, Long joinApplyId) {

        //대기 상태인 것만 취소할 수 있음
        JoinApplyEntity joinApplyEntity = joinApplicationRepository.findByIdAndStatus(
                joinApplyId, JoinStatus.PENDING)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLY));

        //토큰 주인과 엔티티 작성자 일치여부 확인
        if (!joinApplyEntity.getMember().getId()
            .equals(jwtUtil.extractUserId(token.substring("Bearer ".length())))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS);
        }

        joinApplicationRepository.delete(joinApplyEntity);
    }
}
