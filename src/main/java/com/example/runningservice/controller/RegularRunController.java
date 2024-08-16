package com.example.runningservice.controller;

import com.example.runningservice.dto.regular_run.RegularRunRequestDto;
import com.example.runningservice.service.RegularRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/crew/{crewId}/regular")
    public ResponseEntity<?> createRegularRun(@PathVariable("crewId") Long crewId,
        @RequestBody RegularRunRequestDto request) {

        return ResponseEntity.ok(regularRunService.createRegularRun(crewId, request));
    }

    /**
     * 크루의 정기러닝 수정
     */
    @PutMapping("/crew/{crewId}/regular/{regularId}")
    public ResponseEntity<?> createRegularRun(@PathVariable("crewId") Long crewId,
        @PathVariable("regularId") Long regularId,
        @RequestBody RegularRunRequestDto request) {

        return ResponseEntity.ok(regularRunService.updateRegularRun(regularId, request));
    }
}
