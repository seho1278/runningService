package com.example.runningservice.controller;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.RunRecordService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run")
public class RunRecordController {

    @Autowired
    private RunRecordService runRecordService;

    @Autowired
    private MemberRepository memberRepository;

    // 러닝 누적 기록 조회
    @GetMapping("/{userId}/total")
    public ResponseEntity<List<RunRecordEntity>> getRunRecordsByUserId(@PathVariable Long userId) {
        Optional<MemberEntity> user = memberRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<RunRecordEntity> runRecords = runRecordService.findByUserId(user.get());
        return ResponseEntity.ok(runRecords);
    }

    // 러닝 누적 기록 조회 --> ?

    // 러닝 기록 조회
    @GetMapping("/records/{runningId}")
    public ResponseEntity<RunRecordEntity> getRunRecordById(@PathVariable Long runningId) {
        return runRecordService.findById(runningId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // 러닝 기록 생성
    @PostMapping("/records")
    public RunRecordEntity createRunRecord(@RequestBody RunRecordEntity runRecord) {
        return runRecordService.save(runRecord);
    }

    // 러닝 기록 수정
    @PutMapping("/{userId}")
    public ResponseEntity<RunRecordEntity> updateRunRecord(
        @PathVariable Long userId, @RequestBody RunRecordEntity updatedRunRecord) {
        return runRecordService.findById(userId)
            .map(runRecord -> {
                updatedRunRecord.setId(userId);
                return ResponseEntity.ok(runRecordService.save(updatedRunRecord));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // 러닝 기록 삭제
    @DeleteMapping("/records/{runningId}")
    public ResponseEntity<Void> deleteRunRecord(@PathVariable Long runningId) {
        if (runRecordService.findById(runningId).isPresent()) {
            runRecordService.deleteById(runningId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
