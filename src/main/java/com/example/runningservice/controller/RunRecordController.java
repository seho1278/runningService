package com.example.runningservice.controller;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.service.RunRecordService;
import com.example.runningservice.util.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run")
public class RunRecordController {

    private final RunRecordService runRecordService;

    public RunRecordController(RunRecordService runRecordService) {
        this.runRecordService = runRecordService;
    }

    /**
     * 러닝 누적 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<List<RunRecordResponseDto>> getRunRecordsByUserId(@LoginUser Long userId) {
        List<RunRecordResponseDto> runRecords = runRecordService.findByUserId(userId);
        return ResponseEntity.ok(runRecords);
    }

    /**
     * 러닝 누적 기록 조회 : 페이스 average, 나머지는 합으로
      */
    @GetMapping("/total")
    public ResponseEntity<RunRecordResponseDto> getAccumulatedRunrecordByUserId(@LoginUser Long userId) {

        RunRecordResponseDto totalRecord = runRecordService.calculateTotalRunRecords(userId);

        if (totalRecord == null) {
            return ResponseEntity.noContent().build(); // 기록이 없을 경우 204 No Content 반환
        }

        return ResponseEntity.ok(totalRecord);
    }

    /**
     * 러닝 기록 조회
      */
    @GetMapping("/records/{runningId}")
    public ResponseEntity<RunRecordResponseDto> getRunRecordById(@PathVariable Long runningId) {
        return runRecordService.findById(runningId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 러닝 기록 생성
      */
    @PostMapping("/records")
    public ResponseEntity<RunRecordResponseDto> createRunRecord(@LoginUser Long userId, @RequestBody RunRecordRequestDto runRecord) {
        RunRecordResponseDto createdRecord = runRecordService.createRunRecord(userId, runRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord); // 201 Created 반환
    }

    /**
     * 러닝 기록 수정
      */
    @PutMapping("/records/{runRecordId}")
    public ResponseEntity<RunRecordResponseDto> updateRunRecord(
        @PathVariable Long runRecordId, @RequestBody RunRecordRequestDto updatedRunRecord) {
        return ResponseEntity.ok(runRecordService.updateRunRecord(runRecordId, updatedRunRecord));
    }

    /**
     * 러닝 기록 삭제
      */
    @DeleteMapping("/records/{runRecordId}")
    public ResponseEntity<Void> deleteRunRecord(@PathVariable Long runRecordId) {
        if (runRecordService.findById(runRecordId).isPresent()) {
            runRecordService.deleteById(runRecordId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
