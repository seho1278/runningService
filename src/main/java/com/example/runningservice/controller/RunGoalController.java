package com.example.runningservice.controller;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.service.RunGoalService;
import com.example.runningservice.util.LoginUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run/goals")
public class RunGoalController {

    private final RunGoalService runGoalService;

    public RunGoalController(RunGoalService runGoalService) {
        this.runGoalService = runGoalService;
    }

    @GetMapping
    public ResponseEntity<List<RunGoalResponseDto>> getAllRunGoals() {
        List<RunGoalResponseDto> runGoals = runGoalService.findAll();
        return ResponseEntity.ok(runGoals);
    }

    @GetMapping("/{runningId}")
    public ResponseEntity<RunGoalResponseDto> getRunGoalById(@PathVariable Long runningId) {
        RunGoalResponseDto runGoal = runGoalService.findById(runningId);
        return ResponseEntity.ok(runGoal);
    }

    @GetMapping("/my")
    public ResponseEntity<List<RunGoalResponseDto>> getRunGoalByUserId(@LoginUser Long loginId) {
        List<RunGoalResponseDto> runGoals = runGoalService.findByUserId(loginId);
        return ResponseEntity.ok(runGoals);
    }

    @PostMapping
    public ResponseEntity<RunGoalResponseDto> createRunGoal(@RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoal = runGoalService.createRunGoal(runGoalRequestDto);
        return ResponseEntity.status(201).body(runGoal);
    }

    @PutMapping("/{runningId}")
    public ResponseEntity<RunGoalResponseDto> updateRunGoal(
        @PathVariable Long runningId, @RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoal = runGoalService.updateRunGoal(runningId, runGoalRequestDto);
        return ResponseEntity.ok(runGoal);
    }

    @DeleteMapping("/{runningId}")
    public ResponseEntity<Void> deleteRunGoal(@PathVariable Long runningId) {
        runGoalService.deleteById(runningId);
        return ResponseEntity.noContent().build();
    }
}
