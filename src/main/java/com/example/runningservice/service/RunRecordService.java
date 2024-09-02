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

        RunRecordEntity updatedEntity = RunRecordEntity.builder()
            .id(existingEntity.getId())
            .userId(existingEntity.getUserId())
            .goalId(runGoalRepository.getReferenceById(requestDto.getGoalId()))
            .distance(requestDto.getDistance())
            .runningTime(requestDto.getRunningTime())
            .pace(requestDto.getPace())
            .createdAt(existingEntity.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isPublic(requestDto.getIsPublic())
            .build();

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

        int totalRunningTime = runRecords.stream()
            .mapToInt(RunRecordEntity::getRunningTime)
            .sum();

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
