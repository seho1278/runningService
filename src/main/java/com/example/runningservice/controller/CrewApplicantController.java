package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.crewMember.CrewMemberResponseDto;
import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.service.CrewApplicantService;
import com.example.runningservice.util.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crew")
@Slf4j
public class CrewApplicantController {

    private final CrewApplicantService crewApplicantService;

    @GetMapping("/{crew_id}/join/list")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<Page<CrewApplicantResponseDto>> getJoinApplications(
        @LoginUser Long userId,
        @PathVariable("crew_id") Long crewId,
        @RequestParam JoinStatus status, Pageable pageable) {

        GetApplicantsRequestDto requestDto = GetApplicantsRequestDto.builder()
            .status(status)
            .pageable(pageable)
            .build();

        return ResponseEntity.ok(
            crewApplicantService.getAllJoinApplications(crewId, requestDto));
    }

    @GetMapping("/{crew_id}/join")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewApplicantDetailResponseDto> getJoinApplication(
        @LoginUser Long userId,
        @PathVariable("crew_id") Long crewId,
        @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(
            crewApplicantService.getJoinApplicationDetail(crewId, joinApplyId));
    }

    @PostMapping("/{crew_id}/join-approval")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewMemberResponseDto> approveJoin(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId, @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(
            crewApplicantService.approveJoinApplication(joinApplyId));
    }

    @PutMapping("/{crew_id}/join-reject")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<String> rejectJoin(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId, @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(crewApplicantService.rejectJoinApplication(joinApplyId));
    }
}
