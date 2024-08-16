package com.example.runningservice.service;

import com.example.runningservice.dto.JoinApplyDto;
import com.example.runningservice.dto.JoinApplyDto.SimpleResponse;
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
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.JwtUtil;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
        JoinApplyDto.Request joinRequestForm) {
        MemberEntity memberEntity = memberRepository.findById(joinRequestForm.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        //멤버제한 조건 검증 : 성별(선택), 최소연령(선택), 최대연령(선택), 러닝기록 오픈여부
        isJoinPossible(memberEntity, crewEntity);

        //crew의 가입승인 필수여부 확인 & 가입승인 필수 아닐 시, 자동으로 가입
        JoinApplyEntity joinApplyEntity = JoinApplyEntity.of(memberEntity, crewEntity,
            joinRequestForm.getMessage());

        if (!crewEntity.getLeaderRequired()) { // 가입 승인이 필요 없는 경우
            // 가입 상태를 승인으로 설정
            joinApplyEntity.markStatus(JoinStatus.APPROVED);

            // 크루원으로 자동 가입 처리
            CrewMemberEntity crewMemberEntity = CrewMemberEntity.memberOf(memberEntity, crewEntity);
            crewMemberRepository.save(crewMemberEntity);
        } else {
            // 가입 승인이 필요한 경우
            joinApplyEntity.markStatus(JoinStatus.PENDING);
        }
        // 엔티티 저장
        JoinApplyEntity savedJoinApplyEntity = joinApplicationRepository.save(joinApplyEntity);

        return JoinApplyDto.DetailResponse.from(savedJoinApplyEntity);
    }

    @Transactional(readOnly = true)
    public List<SimpleResponse> getJoinApplications(String token, Long memberId) {
        token = token.substring("Bearer ".length());
        if (!jwtUtil.validateToken(memberId, token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS);
        }
        // memberId 기준 가입신청 리스트 조회
        return joinApplicationRepository.findAllByMember_Id(memberId).stream()
            .map(SimpleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public JoinApplyDto.DetailResponse getJoinApplicationDetail(String token, Long userId,
        Long joinApplyId) {
        token = token.substring("Bearer ".length());
        if (!jwtUtil.validateToken(userId, token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS);
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
//        Boolean requireRecordOpen = crewEntity.getRunRecordOpen();
//        if (requireRecordOpen && memberEntity.getRunRecordOpen().equals(Visibility.PUBLIC)) {
//            throw new CustomException("가입 자격이 없습니다. 달리기 기록을 공개해야 합니다.");
//        }
    }

    private void validateAge(MemberEntity memberEntity, CrewEntity crewEntity, Integer minAge,
        Integer maxAge) {

        if (minAge != null || maxAge != null) {
            if (memberEntity.getBirthYear() == null || memberEntity.getBirthYearVisibility()
                .equals(Visibility.PRIVATE)) {
                throw new CustomException(ErrorCode.AGE_REQUIRED);
            }

            int memberAge = LocalDate.now().getYear() - memberEntity.getBirthYear() + 1;
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
            if (memberGender == null || memberGenderVisibility.equals(Visibility.PRIVATE)) {
                throw new CustomException(ErrorCode.GENDER_REQUIRED);
            }
            if (!memberGender.equals(requiredGender)) {
                throw new CustomException(ErrorCode.GENDER_RESTRICTION_NOT_MET);
            }
        }
    }
}
