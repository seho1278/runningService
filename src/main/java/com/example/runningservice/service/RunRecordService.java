package com.example.runningservice.service;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.RunRecordRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final MemberRepository memberRepository;
    private final RunGoalRepository runGoalRepository;

    // 생성자 인젝션을 통해 의존성을 주입
    public RunRecordService(RunRecordRepository runRecordRepository, MemberRepository memberRepository, RunGoalRepository runGoalrepository) {
        this.runRecordRepository = runRecordRepository;
        this.memberRepository = memberRepository;
        this.runGoalRepository = runGoalrepository;
    }

    public List<RunRecordResponseDto> findByUserId(Long userId) {
        List<RunRecordEntity> runRecords = runRecordRepository.findByUserId_Id(userId);
        return runRecords.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

    public RunRecordResponseDto createRunRecord(Long userId, RunRecordRequestDto requestDto) {
        RunRecordEntity runRecordEntity = RunRecordEntity.builder()
            .userId(memberRepository.findMemberById(userId))
            .goalId(runGoalRepository.getReferenceById(requestDto.getGoalId()))
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
            .orElseThrow(() -> new NoSuchElementException("해당 기록을 찾을수 없습니다."));

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
            return null;
        }

        int totalDistance = 0;
        long totalRunningTimeSeconds = 0;
        long totalPaceSeconds = 0;

        for (RunRecordEntity record : runRecords) {
            totalDistance += record.getDistance();
            totalRunningTimeSeconds += record.getRunningTime().toLocalTime().toSecondOfDay();
            totalPaceSeconds += record.getPace().toLocalTime().toSecondOfDay();
        }

        int recordCount = runRecords.size();
        long avgPaceSeconds = totalPaceSeconds / recordCount;

        return RunRecordResponseDto.builder()
            .userId(userId)
            .distance(totalDistance)
            .runningTime(LocalDateTime.from(LocalTime.ofSecondOfDay(totalRunningTimeSeconds)))
            .pace(LocalDateTime.from(LocalTime.ofSecondOfDay(avgPaceSeconds)))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(1) // 임의로 공개 설정
            .build();
    }

    private RunRecordResponseDto entityToDto(RunRecordEntity entity) {
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
