package com.example.runningservice.controller;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.service.RunRecordService;
import com.example.runningservice.util.LoginUser;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/run/records")
public class RunRecordController {

    private final RunRecordService runRecordService;

    /**
     * 러닝 누적 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<List<RunRecordResponseDto>> getRunRecordsByUserId(
        @LoginUser Long userId) {
        List<RunRecordResponseDto> runRecords = runRecordService.findByUserId(userId);
        return ResponseEntity.ok(runRecords);
    }

    /**
     * 러닝 누적 기록 조회 : 페이스 average, 나머지는 합으로
     */
    @GetMapping("/total")
    public ResponseEntity<RunRecordResponseDto> getAccumulatedRunrecordByUserId(
        @LoginUser Long userId) {

        RunRecordResponseDto totalRecord = runRecordService.calculateTotalRunRecords(userId);

        if (totalRecord == null) {
            return ResponseEntity.noContent().build(); // 기록이 없을 경우 204 No Content 반환
        }

        return ResponseEntity.ok(totalRecord);
    }

    /**
     * 러닝 기록 조회
     */
    @GetMapping("/{runningId}")
    public ResponseEntity<RunRecordResponseDto> getRunRecordById(@PathVariable Long runningId) {
        return runRecordService.findById(runningId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 러닝 기록 생성
     */
    @PostMapping("")
    public ResponseEntity<RunRecordResponseDto> createRunRecord(@LoginUser Long userId, @RequestBody RunRecordRequestDto runRecord) {
        RunRecordResponseDto createdRecord = runRecordService.createRunRecord(userId, runRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord); // 201 Created 반환
    }

    /**
     * 러닝 기록 수정
     */
    @PutMapping("/{runRecordId}")
    public ResponseEntity<RunRecordResponseDto> updateRunRecord(
        @PathVariable Long runRecordId, @RequestBody RunRecordRequestDto updatedRunRecord) {
        return ResponseEntity.ok(runRecordService.updateRunRecord(runRecordId, updatedRunRecord));
    }

    /**
     * 러닝 기록 삭제
     */
    @DeleteMapping("/{runRecordId}")
    public ResponseEntity<Void> deleteRunRecord(@PathVariable Long runRecordId) {
        if (runRecordService.findById(runRecordId).isPresent()) {
            runRecordService.deleteById(runRecordId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 러닝 기록 공개 설정
     */
    @PutMapping("/visibility/{runRecordId}")
    public ResponseEntity<RunRecordResponseDto> updateRunRecordVisibility(
        @PathVariable Long runRecordId, @RequestBody RunRecordRequestDto updatedRunRecord) {
        return ResponseEntity.ok(runRecordService.updateRunRecord(runRecordId, updatedRunRecord));
    }

    /**
     * 기간 동안의 총 거리 API
     */
    @GetMapping("/total-distance")
    public double getTotalDistance(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return runRecordService.getTotalDistanceForPeriod(startDate, endDate);
    }

    /**
     * 기간 동안의 총 시간 API
     */
    @GetMapping("/total-time")
    public int getTotalTime(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return runRecordService.getTotalTimeForPeriod(startDate, endDate);
    }

    /**
     * 기간 동안의 모든 기록 조회 API
     */
    @GetMapping("/records")
    public List<RunRecordEntity> getRunningRecords(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return runRecordService.getRecordsForPeriod(startDate, endDate);
    }


}
