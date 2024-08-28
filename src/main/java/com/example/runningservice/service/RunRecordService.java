package com.example.runningservice.service;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.RunRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final MemberRepository memberRepository;
    private final RunGoalRepository runGoalRepository;

    @Autowired
    public RunRecordService(RunRecordRepository runRecordRepository, MemberRepository memberRepository, RunGoalRepository runGoalRepository) {
        this.runRecordRepository = runRecordRepository;
        this.memberRepository = memberRepository;
        this.runGoalRepository = runGoalRepository;
    }

    public List<RunRecordResponseDto> findByUserId(Long userId) {
        List<RunRecordEntity> runRecords = runRecordRepository.findByUserId_Id(userId);

        return runRecords.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

    public RunRecordResponseDto createRunRecord(Long userId, RunRecordRequestDto requestDto) {
        MemberEntity member = memberRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        RunGoalEntity goal = runGoalRepository.findById(requestDto.getGoalId())
            .orElseThrow(() -> new NoSuchElementException("목표를 찾을 수 없습니다."));

        RunRecordEntity runRecordEntity = RunRecordEntity.builder()
            .userId(member)
            .goalId(goal)
            .distance(requestDto.getDistance())
            .runningTime(requestDto.getRunningTime())
            .pace(requestDto.getPace())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(requestDto.getIsPublic())
            .build();

        RunRecordEntity savedEntity = runRecordRepository.save(runRecordEntity);
        return entityToDto(savedEntity);
    }

    public RunRecordResponseDto updateRunRecord(Long runningId, RunRecordRequestDto requestDto) {
        RunRecordEntity existingEntity = runRecordRepository
            .findById(runningId)
            .orElseThrow(() -> new NoSuchElementException("해당 기록을 찾을 수 없습니다."));

        // 빌더 패턴을 사용하여 새 객체를 생성합니다.
        RunRecordEntity updatedEntity = RunRecordEntity.builder()
            .id(existingEntity.getId()) // 기존 ID 유지
            .userId(existingEntity.getUserId()) // 기존 사용자 유지
            .goalId(runGoalRepository.getReferenceById(requestDto.getGoalId())) // 새로운 목표 설정
            .distance(requestDto.getDistance()) // 거리 설정
            .runningTime(requestDto.getRunningTime()) // 실행 시간 설정
            .pace(requestDto.getPace()) // 페이스 설정
            .createdAt(existingEntity.getCreatedAt()) // 생성 일자 유지
            .updatedAt(LocalDateTime.now()) // 현재 시간으로 업데이트
            .isPublic(requestDto.getIsPublic()) // 공개 여부 설정
            .build(); // 빌더 패턴으로 객체 생성

        RunRecordEntity savedEntity = runRecordRepository.save(updatedEntity);
        return entityToDto(savedEntity);
    }


    public Optional<RunRecordResponseDto> findById(Long id) {
        return runRecordRepository.findById(id).map(this::entityToDto);
    }

    public void deleteById(Long id) {
        runRecordRepository.deleteById(id);
    }

    public RunRecordResponseDto calculateTotalRunRecords(Long userId) {
        List<RunRecordEntity> runRecords = runRecordRepository.findByUserId_Id(userId);

        if (runRecords.isEmpty()) {
            throw new NoSuchElementException("런 기록이 존재하지 않습니다.");
        }

        int totalDistance = runRecords.stream()
            .mapToInt(RunRecordEntity::getDistance)
            .sum();

        Duration totalRunningTime = runRecords.stream()
            .map(RunRecordEntity::getRunningTime)
            .reduce(Duration.ZERO, Duration::plus);

        Duration totalPace = runRecords.stream()
            .map(RunRecordEntity::getPace)
            .reduce(Duration.ZERO, Duration::plus);

        Duration averagePace = totalPace.dividedBy(runRecords.size());

        return RunRecordResponseDto.builder()
            .userId(userId)
            .distance(totalDistance)
            .runningTime(totalRunningTime)
            .pace(averagePace)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(1) // 임의로 공개 설정
            .build();
    }

    public RunRecordResponseDto entityToDto(RunRecordEntity entity) {
        if (entity == null || entity.getUserId() == null) {
            throw new IllegalArgumentException("러닝 기록이 없거나 유저정보가 없습니다.");
        }
        return RunRecordResponseDto.builder()
            .id(entity.getId())
            .userId(entity.getUserId().getId())
            .distance(entity.getDistance())
            .runningTime(entity.getRunningTime())
            .pace(entity.getPace())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .isPublic(entity.getIsPublic())
            .build();
    }

}
