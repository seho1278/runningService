package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.crewMember.CrewMemberResponseDetailDto;
import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantSimpleResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.service.CrewApplicantService;
import com.example.runningservice.service.CrewMemberService;
import com.example.runningservice.service.ProfileWithRunService;
import com.example.runningservice.util.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crew")
@Slf4j
public class CrewApplicantController {

    private final CrewApplicantService crewApplicantService;
    private final CrewMemberService crewMemberService;
    private final ProfileWithRunService profileWithRunService;

    /**
     * 크루 신청자 조회
     */
    @GetMapping("/{crew_id}/join/list")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<Page<CrewApplicantSimpleResponseDto>> getJoinApplications(
        @LoginUser Long userId,
        @PathVariable("crew_id") Long crewId,
        @RequestParam(required = false) JoinStatus status,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.ASC) Pageable pageable) {

        GetApplicantsRequestDto requestDto = GetApplicantsRequestDto.builder()
            .status(status)
            .pageable(pageable)
            .build();

        return ResponseEntity.ok(
            crewApplicantService.getAllJoinApplications(crewId, requestDto));
    }

    /**
     * 크루 가입신청 개별 조회
     */
    @GetMapping("/{crew_id}/join")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewApplicantDetailResponseDto> getJoinApplication(
        @LoginUser Long userId,
        @PathVariable("crew_id") Long crewId,
        @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(
            crewApplicantService.getJoinApplicationDetail(crewId, joinApplyId));
    }

    /**
     * 크루 가입 승인
     */
    @PostMapping("/{crew_id}/approve-join")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewMemberResponseDetailDto> approveJoin(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId, @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(profileWithRunService.getCrewMemberWithEntity(
            crewApplicantService.approveJoinApplication(joinApplyId)));
    }

    /**
     * 크루 가입 거부
     */
    @PatchMapping("/{crew_id}/reject-join")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewApplicantDetailResponseDto> rejectJoin(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId, @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(CrewApplicantDetailResponseDto.of(
            crewApplicantService.rejectJoinApplication(joinApplyId)));
    }
}
