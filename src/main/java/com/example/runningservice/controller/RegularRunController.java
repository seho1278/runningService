package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.regular_run.CrewRegularRunResponseDto;
import com.example.runningservice.dto.regular_run.RegularRunRequestDto;
import com.example.runningservice.dto.regular_run.RegularRunResponseDto;
import com.example.runningservice.service.RegularRunService;
import com.example.runningservice.util.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegularRunController {

    private final RegularRunService regularRunService;

    /**
     * 크루의 정기러닝 생성
     */
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    @PostMapping("/crew/{crewId}/regular")
    public ResponseEntity<RegularRunResponseDto> createRegularRun(@LoginUser Long loginId,
        @PathVariable("crewId") Long crewId,
        @RequestBody RegularRunRequestDto request) {

        return ResponseEntity.ok(regularRunService.createRegularRun(crewId, request));
    }

    /**
     * 크루의 정기러닝 수정
     */
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    @PutMapping("/crew/{crewId}/regular/{regularId}")
    public ResponseEntity<RegularRunResponseDto> createRegularRun(@LoginUser Long loginId,
        @PathVariable("crewId") Long crewId,
        @PathVariable("regularId") Long regularId,
        @RequestBody RegularRunRequestDto request) {

        return ResponseEntity.ok(
            regularRunService.updateRegularRun(regularId, request));
    }

    /**
     * 크루의 정기러닝 삭제
     */
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    @DeleteMapping("/crew/{crewId}/regular/{regularId}")
    public ResponseEntity<RegularRunResponseDto> deleteRegularRun(@LoginUser Long loginId,
        @PathVariable("crewId") Long crewId,
        @PathVariable("regularId") Long regularId) {

        return ResponseEntity.ok(
            regularRunService.deleteRegularRun(regularId));
    }

    /**
     * 크루별 정기러닝 정보 조회
     */
    @GetMapping("/crew/regular")
    public ResponseEntity<List<CrewRegularRunResponseDto>> getRegularRunList(Pageable pageable) {
        return ResponseEntity.ok(regularRunService.getRegularRunList(pageable));
    }

    /**
     * 특정 크루 정기 러닝 정보 조회
     */
    @GetMapping("/crew/{crewId}/regular")
    public ResponseEntity<CrewRegularRunResponseDto> getCrewRegularRunList(
        @PathVariable("crewId") Long crewId, Pageable pageable) {
        return ResponseEntity.ok(regularRunService.getCrewRegularRunList(crewId, pageable));
    }

    /**
     * 특정 정기 러닝 정보 조회
     */
    @GetMapping("/crew/{crewId}/regular/{regularId}")
    public ResponseEntity<RegularRunResponseDto> getRegularRun(
        @PathVariable("crewId") Long crewId, @PathVariable("regularId") Long regularId) {
        return ResponseEntity.ok(regularRunService.getRegularRun(regularId));
    }
}
