package com.example.runningservice.service;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.MemberRepository;
import java.util.HashMap;
import java.util.Map;
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
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RUN_GOAL));

        return entityToDto(entity);
    }


    public List<RunGoalResponseDto> findByUserId(Long id) {
        List<RunGoalEntity> runGoals = runGoalRepository.findByUserId_Id(id);
        return runGoals.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

    public RunGoalResponseDto createRunGoal(Long userId, RunGoalRequestDto requestDto) {
        Map<String, Integer> map = transformDTO(requestDto);

        RunGoalEntity runGoalEntity = RunGoalEntity.builder()

            .userId(memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
            .totalDistance(requestDto.getTotalDistance())
            .totalRunningTime(map.get("totalRunningTime"))
            .averagePace(map.get("averagePace"))
            .runCount(requestDto.getRunCount())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        RunGoalEntity savedEntity = runGoalRepository.save(runGoalEntity);
        return entityToDto(savedEntity);
    }

    public RunGoalResponseDto updateRunGoal(Long id, RunGoalRequestDto requestDto) {
        RunGoalEntity existingEntity = runGoalRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RUN_GOAL));

        Map<String, Integer> map = transformDTO(requestDto);

        RunGoalEntity updatedEntity = RunGoalEntity.builder()
            .id(existingEntity.getId())
            .userId(existingEntity.getUserId())
            .totalDistance(requestDto.getTotalDistance())
            .totalRunningTime(map.get("totalRunningTime"))
            .averagePace(map.get("averagePace"))
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
            .userId(
                entity.getUserId() != null ? entity.getUserId().getId() : null) // Null check added
            .totalDistance(entity.getTotalDistance())
            .totalRunningTime(entity.getTotalRunningTime())
            .averagePace(entity.getAveragePace())
            .runCount(entity.getRunCount())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public Map<String,Integer> transformDTO (RunGoalRequestDto runGoalRequestDto) {

        Map<String,Integer> map = new HashMap<>();
        // runningTime 시:분:초 -> sec 변환
        String[] runningTimes = runGoalRequestDto.getTotalRunningTime().split(":");
        int runningTime = Integer.parseInt(runningTimes[0])*3600+Integer.parseInt(runningTimes[1])*60+Integer.parseInt(runningTimes[2]);

        map.put("totalRunningTime", runningTime);

        // pace 분:초 -> sec 변환
        String[] paces = runGoalRequestDto.getAveragePace().split(":");
        int pace = Integer.parseInt(paces[0])*60+Integer.parseInt(paces[1]);
        map.put("AveragePace", pace);

        return map;
    }
}
