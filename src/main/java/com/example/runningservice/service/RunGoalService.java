package com.example.runningservice.service;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RunGoalService {

    private final RunGoalRepository runGoalRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public RunGoalService(RunGoalRepository runGoalRepository, MemberRepository memberRepository) {
        this.runGoalRepository = runGoalRepository;
        this.memberRepository = memberRepository;
    }

    public List<RunGoalResponseDto> findAll() {
        return runGoalRepository.findAll().stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

    public RunGoalResponseDto findById(Long id) {
        RunGoalEntity entity = runGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("러닝 목표를 찾을 수 없습니다."));

        return entityToDto(entity);
    }


    public List<RunGoalResponseDto> findByUserId(Long id) {
        List<RunGoalEntity> runGoals= runGoalRepository.findByUserId_Id(id);
        return runGoals.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

    public RunGoalResponseDto createRunGoal(RunGoalRequestDto requestDto) {
        RunGoalEntity runGoalEntity = RunGoalEntity.builder()
            .userId(memberRepository.findById(requestDto.getUserId()).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다.")))
            .totalDistance(requestDto.getTotalDistance())
            .totalRunningTime(requestDto.getTotalRunningTime())
            .averagePace(requestDto.getAveragePace())
            .isPublic(requestDto.getIsPublic())
            .runCount(requestDto.getRunCount())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        RunGoalEntity savedEntity = runGoalRepository.save(runGoalEntity);
        return entityToDto(savedEntity);
    }

    public RunGoalResponseDto updateRunGoal(Long id, RunGoalRequestDto requestDto) {
        RunGoalEntity existingEntity = runGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("RunGoal not found"));

        RunGoalEntity updatedEntity = RunGoalEntity.builder()
            .id(existingEntity.getId())
            .userId(existingEntity.getUserId())
            .totalDistance(requestDto.getTotalDistance())
            .totalRunningTime(requestDto.getTotalRunningTime())
            .averagePace(requestDto.getAveragePace())
            .isPublic(requestDto.getIsPublic())
            .runCount(requestDto.getRunCount())
            .createdAt(existingEntity.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        RunGoalEntity savedEntity = runGoalRepository.save(updatedEntity);
        return entityToDto(savedEntity);
    }


    public void deleteById(Long id) {
        runGoalRepository.deleteById(id);
    }

    private RunGoalResponseDto entityToDto(RunGoalEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("러닝목표 엔티티는 빈 값일 수 없습니다.");
        }

        return RunGoalResponseDto.builder()
            .id(entity.getId())
            .userId(entity.getUserId() != null ? entity.getUserId().getId() : null) // Null check added
            .totalDistance(entity.getTotalDistance())
            .totalRunningTime(entity.getTotalRunningTime())
            .averagePace(entity.getAveragePace())
            .isPublic(entity.getIsPublic())
            .runCount(entity.getRunCount())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
