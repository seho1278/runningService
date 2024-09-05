package com.example.runningservice.controller;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.service.RunGoalService;
import com.example.runningservice.util.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/run/goals")
public class RunGoalController {

    private final RunGoalService runGoalService;
    /**
      * 러닝 목표(전체) 조회
     */
    @GetMapping
    public ResponseEntity<List<RunGoalResponseDto>> getAllRunGoals() {
        List<RunGoalResponseDto> runGoals = runGoalService.findAll();
        return ResponseEntity.ok(runGoals);
    }

    /**
     * 러닝 목표 조회
     */
    @GetMapping("/{runGoalId}")
    public ResponseEntity<RunGoalResponseDto> getRunGoalById(@PathVariable Long runGoalId) {
        RunGoalResponseDto runGoal = runGoalService.findById(runGoalId);
        return ResponseEntity.ok(runGoal);
    }

    /**
     * 사용자 러닝 목표(전체) 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<RunGoalResponseDto>> getRunGoalByUserId(@LoginUser Long loginId) {
        List<RunGoalResponseDto> runGoals = runGoalService.findByUserId(loginId);
        return ResponseEntity.ok(runGoals);
    }

    /**
     * 러닝 목표 생성
     */
    @PostMapping
    public ResponseEntity<RunGoalResponseDto> createRunGoal(@RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoal = runGoalService.createRunGoal(runGoalRequestDto);
        return ResponseEntity.status(201).body(runGoal);
    }

    /**
     * 러닝 목표 수정
     */
    @PutMapping("/{runGoalId}")
    public ResponseEntity<RunGoalResponseDto> updateRunGoal(
        @PathVariable Long runGoalId, @RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoal = runGoalService.updateRunGoal(runGoalId, runGoalRequestDto);
        return ResponseEntity.ok(runGoal);
    }

    /**
     * 러닝 목표 삭제
     */
    @DeleteMapping("/{runGoalId}")
    public ResponseEntity<Void> deleteRunGoal(@PathVariable Long runGoalId) {
        runGoalService.deleteById(runGoalId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 러닝 목표 공개 설정
     */
    @PutMapping("/visibility/{runGoalId}")
    public ResponseEntity<RunGoalResponseDto> updateRunGoalVisibility(@PathVariable Long runGoalId, @RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoalResponseDto =  runGoalService.updateRunGoal(runGoalId, runGoalRequestDto);
        return ResponseEntity.status(201).body(runGoalResponseDto);
    }
}
