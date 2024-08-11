package com.example.runningservice.controller;

import com.example.runningservice.dto.crew.CrewRequestDto;
import com.example.runningservice.dto.crew.CrewResponseDto;
import com.example.runningservice.dto.crew.CrewResponseDto.CrewData;
import com.example.runningservice.service.CrewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<CrewResponseDto.CrewData> createCrew(
        @Valid CrewRequestDto.Create request) {
        request.setLoginUserId(1L); // TODO: 현재 로그인 한 사용자 id로 설정

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(crewService.createCrew(request));
    }

    /**
     * 크루 정보 수정
     */
    @PutMapping("/{crewId}")
    public ResponseEntity<CrewData> updateCrew(@Valid CrewRequestDto.Update request,
        @PathVariable("crewId") Long crewId) {
        request.setUpdateCrewId(crewId);

        return ResponseEntity.ok(crewService.updateCrew(request));
    }

    /**
     * 크루 삭제
     */
    @DeleteMapping("/{crewId}")
    public ResponseEntity<CrewData> deleteCrew(@PathVariable("crewId") Long crewId) {
        return ResponseEntity.ok(crewService.deleteCrew(crewId));
    }
}
