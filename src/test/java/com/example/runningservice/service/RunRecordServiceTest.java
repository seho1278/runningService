package com.example.runningservice.service;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.repository.RunRecordRepository;
import com.example.runningservice.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RunRecordServiceTest {

    @InjectMocks
    private RunRecordService runRecordService;

    @Mock
    private RunRecordRepository runRecordRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRunRecord() {
        Long userId = 1L;
        RunRecordRequestDto requestDto = RunRecordRequestDto.builder()
            .goalId(1L)
            .distance(5)
            .runningTime(LocalDateTime.now())
            .pace(LocalDateTime.now())
            .isPublic(1)
            .build();

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(userId);

        RunGoalEntity runGoalEntity = RunGoalEntity.builder()
            .id(requestDto.getGoalId())
            .build();

        RunRecordEntity runRecordEntity = RunRecordEntity.builder()
            .userId(memberEntity)
            .goalId(runGoalEntity)
            .distance(requestDto.getDistance())
            .runningTime(requestDto.getRunningTime())
            .pace(requestDto.getPace())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(requestDto.getIsPublic())
            .build();

        when(memberRepository.findById(userId)).thenReturn(Optional.of(memberEntity));
        when(runRecordRepository.save(any(RunRecordEntity.class))).thenReturn(runRecordEntity);

        RunRecordResponseDto responseDto = runRecordService.createRunRecord(userId, requestDto);

        assertNotNull(responseDto);
        assertEquals(userId, responseDto.getUserId());
        assertEquals(requestDto.getDistance(), responseDto.getDistance());
        verify(runRecordRepository, times(1)).save(any(RunRecordEntity.class));
    }

    @Test
    public void testUpdateRunRecord() {
        Long runningId = 1L;
        RunRecordRequestDto requestDto = RunRecordRequestDto.builder()
            .goalId(2L)
            .distance(10)
            .runningTime(LocalDateTime.now())
            .pace(LocalDateTime.now())
            .isPublic(0)
            .build();

        RunRecordEntity existingEntity = RunRecordEntity.builder()
            .id(runningId)
            .userId(new MemberEntity())
            .goalId(new RunGoalEntity())
            .distance(5)
            .runningTime(LocalDateTime.now())
            .pace(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(1)
            .build();

        RunRecordEntity updatedEntity = RunRecordEntity.builder()
            .id(runningId)
            .userId(existingEntity.getUserId())
            .goalId(new RunGoalEntity())
            .distance(requestDto.getDistance())
            .runningTime(requestDto.getRunningTime())
            .pace(requestDto.getPace())
            .createdAt(existingEntity.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isPublic(requestDto.getIsPublic())
            .build();

        when(runRecordRepository.findById(runningId)).thenReturn(Optional.of(existingEntity));
        when(runRecordRepository.save(updatedEntity)).thenReturn(updatedEntity);

        RunRecordResponseDto responseDto = runRecordService.updateRunRecord(runningId, requestDto);

        assertNotNull(responseDto);
        assertEquals(requestDto.getDistance(), responseDto.getDistance());
    }
}
