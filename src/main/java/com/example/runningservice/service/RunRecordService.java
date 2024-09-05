package com.example.runningservice.service;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.RunRecordRepository;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final MemberRepository memberRepository;
    private final RunGoalRepository runGoalRepository;

    public List<RunRecordResponseDto> findByUserId(Long userId) {
        List<RunRecordEntity> runRecords = runRecordRepository.findByUserId_Id(userId);

        return runRecords.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

    public RunRecordResponseDto createRunRecord(Long userId, RunRecordRequestDto runRecordRequestDto) {
        MemberEntity member = memberRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));


        RunGoalEntity goal = runGoalRepository.findById(runRecordRequestDto.getGoalId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RUN_GOAL));

        Map<String,Integer> map = transformDTO(runRecordRequestDto);

        RunRecordEntity runRecordEntity = RunRecordEntity.builder()
            .userId(member)
            .goalId(goal)
            .distance(runRecordRequestDto.getDistance())
            .runningTime(map.get("runningTime"))
            .pace(map.get("pace"))
            .runningDate(runRecordRequestDto.getRunningDate())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        RunRecordEntity savedEntity = runRecordRepository.save(runRecordEntity);
        return entityToDto(savedEntity);
    }

    public RunRecordResponseDto updateRunRecord(Long runningId, RunRecordRequestDto runRecordRequestDto) {
        RunRecordEntity existingEntity = runRecordRepository
            .findById(runningId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RUN_RECORD));


        Map<String,Integer> map = transformDTO(runRecordRequestDto);

        RunRecordEntity updatedEntity = RunRecordEntity.builder()
            .id(existingEntity.getId())
            .userId(existingEntity.getUserId())
            .goalId(runGoalRepository.getReferenceById(runRecordRequestDto.getGoalId()))
            .distance(runRecordRequestDto.getDistance())
            .runningTime(map.get("runningTime"))
            .pace(map.get("pace"))
            .runningDate(runRecordRequestDto.getRunningDate())
            .createdAt(existingEntity.getCreatedAt())
            .updatedAt(LocalDateTime.now())
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
            throw new CustomException(ErrorCode.NOT_FOUND_RUN_RECORD);
        }


        double totalDistance = runRecords.stream()
            .mapToDouble(RunRecordEntity::getDistance)
            .sum();

        int totalRunningTime = runRecords.stream()
            .mapToInt(RunRecordEntity::getRunningTime)
            .sum();

        int totalPace = runRecords.stream()
            .mapToInt(RunRecordEntity::getPace)
            .sum();

        int averagePace = totalPace / (runRecords.size());

        return RunRecordResponseDto.builder()
            .userId(userId)
            .distance(totalDistance)
            .runningTime(totalRunningTime)
            .pace(averagePace)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
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
            .runningDate(entity.getRunningDate())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public Map<String,Integer> transformDTO (RunRecordRequestDto runRecordRequestDto) {

        Map<String,Integer> map = new HashMap<>();
        // runningTime 시:분:초 -> sec 변환
        String[] runningTimes = runRecordRequestDto.getRunningTime().split(":");
        int runningTime = Integer.parseInt(runningTimes[0])*3600+Integer.parseInt(runningTimes[1])*60+Integer.parseInt(runningTimes[2]);

        map.put("runningTime", runningTime);

        // pace 분:초 -> sec 변환
        String[] paces = runRecordRequestDto.getPace().split(":");
        int pace = Integer.parseInt(paces[0])*60+Integer.parseInt(paces[1]);
        map.put("pace", pace);

        return map;
    }

}
