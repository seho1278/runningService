package com.example.runningservice.controller;

import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.service.RunGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run/goals")
public class RunGoalController {

    @Autowired
    private RunGoalService runGoalService;

    // 러닝 목표 목록 조회
    @GetMapping
    public List<RunGoalEntity> getAllRunGoals() {
        return runGoalService.findAll();
    }
    
    // 러닝 목표 조회
    @GetMapping("/{userId}")
    public ResponseEntity<RunGoalEntity> getRunGoalById(@PathVariable Long userId) {
        return runGoalService.findById(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // 러닝 목표 생성
    @PostMapping
    public RunGoalEntity createRunGoal(@RequestBody RunGoalEntity runGoal) {
        return runGoalService.save(runGoal);
    }

    // 러닝 목표 수정
    @PutMapping("/{userId}")
    public ResponseEntity<RunGoalEntity> updateRunGoal(
        @PathVariable Long userId, @RequestBody RunGoalEntity updatedRunGoal) {
        return runGoalService.findById(userId)
            .map(runGoal -> {
                updatedRunGoal.setId(userId);
                return ResponseEntity.ok(runGoalService.save(updatedRunGoal));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // 러닝 목표 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteRunGoal(@PathVariable Long userId) {
        if (runGoalService.findById(userId).isPresent()) {
            runGoalService.deleteById(userId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
