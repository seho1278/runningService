package com.example.runningservice.controller;

import com.example.runningservice.dto.crew.CrewBaseResponseDto;
import com.example.runningservice.dto.crew.CrewCreateRequestDto;
import com.example.runningservice.dto.crew.CrewDetailResponseDto;
import com.example.runningservice.dto.crew.CrewFilterDto.CrewInfo;
import com.example.runningservice.dto.crew.CrewJoinStatusResponseDto;
import com.example.runningservice.dto.crew.CrewRoleResponseDto;
import com.example.runningservice.dto.crew.CrewUpdateRequestDto;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.OccupancyStatus;
import com.example.runningservice.enums.Region;
import com.example.runningservice.service.CrewService;
import com.example.runningservice.util.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class CrewController {

    private final CrewService crewService;

    /**
     * 크루 생성
     */
    @PostMapping
    public ResponseEntity<CrewBaseResponseDto> createCrew(@LoginUser Long userId,
        @Valid CrewCreateRequestDto request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(crewService.createCrew(request, userId));
    }

    /**
     * 크루 정보 수정
     */
    @PutMapping("/{crewId}")
    public ResponseEntity<CrewBaseResponseDto> updateCrew(@PathVariable("crewId") Long crewId,
        @Valid CrewUpdateRequestDto request) {
        return ResponseEntity.ok(crewService.updateCrew(request, crewId));
    }

    /**
     * 크루 삭제
     */
    @DeleteMapping("/{crewId}")
    public ResponseEntity<CrewBaseResponseDto> deleteCrew(@PathVariable("crewId") Long crewId) {
        return ResponseEntity.ok(crewService.deleteCrew(crewId));
    }

    /**
     * 크루 싱세 정보 조회
     */
    @GetMapping("/{crewId}")
    public ResponseEntity<CrewDetailResponseDto> getCrew(@LoginUser Long loginId,
        @PathVariable("crewId") Long crewId) {
        return ResponseEntity.ok(crewService.getCrew(loginId, crewId));
    }

    /**
     * 참가 중인 크루 리스트 조회
     */
    @GetMapping("/participate")
    public ResponseEntity<Page<CrewRoleResponseDto>> getParticipateCrewList(@LoginUser Long userId,
        Pageable pageable) {

        return ResponseEntity.ok(crewService.getParticipateCrewList(userId, pageable));
    }

    /**
     * 전체 크루 필터링 조회
     */
    @GetMapping
    public ResponseEntity<Page<CrewJoinStatusResponseDto>> getCrewList(@LoginUser Long userId,
        @RequestParam(value = "activityRegion", required = false) Region activityRegion,
        @RequestParam(value = "minYear", required = false) Integer minYear,
        @RequestParam(value = "maxYear", required = false) Integer maxYear,
        @RequestParam(value = "gender", required = false) Gender gender,
        @RequestParam(value = "runRecordPublic", required = false) Boolean runRecordPublic,
        @RequestParam(value = "leaderRequired", required = false) Boolean leaderRequired,
        @RequestParam(value = "occupancyStatus", required = false) OccupancyStatus occupancyStatus,
        Pageable pageable
    ) {
        return ResponseEntity.ok(crewService.getCrewList(userId, CrewInfo.builder()
            .activityRegion(activityRegion)
            .leaderRequired(leaderRequired)
            .runRecordPublic(runRecordPublic)
            .maxYear(maxYear)
            .minYear(minYear)
            .gender(gender)
            .occupancyStatus(occupancyStatus)
            .build(), pageable));
    }
}
