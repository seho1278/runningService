package com.example.runningservice.controller;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.service.RunGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run-goals")
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

    @GetMapping("/{id}")
    public ResponseEntity<RunGoalResponseDto> getRunGoalById(@PathVariable Long id) {
        RunGoalResponseDto runGoal = runGoalService.findById(id);
        return ResponseEntity.ok(runGoal);
    }

    @PostMapping
    public ResponseEntity<RunGoalResponseDto> createRunGoal(@RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoal = runGoalService.createRunGoal(runGoalRequestDto);
        return ResponseEntity.status(201).body(runGoal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RunGoalResponseDto> updateRunGoal(
        @PathVariable Long id, @RequestBody RunGoalRequestDto runGoalRequestDto) {
        RunGoalResponseDto runGoal = runGoalService.updateRunGoal(id, runGoalRequestDto);
        return ResponseEntity.ok(runGoal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRunGoal(@PathVariable Long id) {
        runGoalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
