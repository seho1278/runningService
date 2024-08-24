package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.crewMember.ChangeCrewRoleRequestDto;
import com.example.runningservice.dto.crewMember.ChangeCrewRoleResponseDto;
import com.example.runningservice.dto.crewMember.ChangedLeaderResponseDto;
import com.example.runningservice.dto.crewMember.CrewMemberBlackListResponseDto;
import com.example.runningservice.dto.crewMember.GetCrewMemberRequestDto;
import com.example.runningservice.dto.crewMember.CrewMemberResponseDto;
import com.example.runningservice.service.CrewMemberService;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("crew")
public class CrewMemberController {

    private final CrewMemberService crewMemberService;
    private final AESUtil aesUtil;

    /**
     * 크루원 리스트 조회
     */
    @GetMapping("/{crew_id}/member/list")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<Page<CrewMemberResponseDto>> getCrewMembers(@LoginUser Long userId,
        @PathVariable Long crewId,
        @Valid GetCrewMemberRequestDto.Filter filterDto,
        Pageable pageable) {

        Page<CrewMemberResponseDto> pageDto = crewMemberService.getCrewMembers(crewId, filterDto,
            pageable).map(e -> CrewMemberResponseDto.of(e, aesUtil));

        return ResponseEntity.ok(pageDto);
    }

    /**
     * 크루원 개별 상세조회
     */
    @GetMapping("/{crew_id}/member")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewMemberResponseDto> getCrewMember(@LoginUser Long userID,
        @PathVariable("crew_id") Long crewId, @RequestParam Long crewMemberId) {

        CrewMemberResponseDto crewMemberDto = CrewMemberResponseDto.of(
            crewMemberService.getCrewMember(crewMemberId), aesUtil);

        return ResponseEntity.ok(crewMemberDto);
    }

    /**
     * 리더 권한 위임
     */
    @PatchMapping("/{crew_id}/transfer-leader")
    @CrewRoleCheck(role = {"LEADER"})
    public ResponseEntity<ChangedLeaderResponseDto> transferLeaderRole(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId,
        @RequestParam Long crewMemberId) {

        return ResponseEntity.ok(
            crewMemberService.transferLeaderRole(userId, crewId, crewMemberId));
    }

    /**
     * 크루원 권한 변경
     */
    @PatchMapping("/{crew_id}/change-role")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<ChangeCrewRoleResponseDto> changeRole(@LoginUser Long userID,
        @PathVariable("crew_id") Long crewId,
        @RequestBody @Valid ChangeCrewRoleRequestDto requestDto) {

        return ResponseEntity.ok(
            ChangeCrewRoleResponseDto.of(crewMemberService.changeRole(requestDto)));
    }

    /**
     * 강제 퇴장
     */
    @DeleteMapping("/{crew_id}/remove-member")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<CrewMemberBlackListResponseDto> removeMember(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId, @RequestParam Long crewMemberId) {

        return ResponseEntity.ok(CrewMemberBlackListResponseDto.of(
            crewMemberService.removeCrewMember(crewId, crewMemberId)));
    }

    /**
     * 크루 퇴장
     */
    @DeleteMapping("/{crew_id}/leave")
    @CrewRoleCheck(role = {"MEMBER", "STAFF"}) //Leader는 자발적 퇴장 불가
    public ResponseEntity<Void> leaveCrew(@LoginUser Long userId,
        @PathVariable("crew_id") Long crewId) {

        crewMemberService.leaveCrew(crewId, userId);

        return ResponseEntity.noContent().build();
    }
}

