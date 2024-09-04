package com.example.runningservice.service;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.MemberRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RunGoalServiceTest {

    @Mock
    private RunGoalRepository runGoalRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RunGoalService runGoalService;

    private RunGoalEntity runGoalEntity;
    private MemberEntity memberEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        memberEntity = MemberEntity.builder()
            .id(1L)
            .name("Test User")
            .build();

        runGoalEntity = RunGoalEntity.builder()
            .id(1L)
            .userId(memberEntity)
            .totalDistance(10.0)
            .totalRunningTime(3600)
            .averagePace(300)
            .isPublic(1)
            .runCount(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    void testFindAll() {
        when(runGoalRepository.findAll()).thenReturn(Collections.singletonList(runGoalEntity));

        List<RunGoalResponseDto> result = runGoalService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(runGoalEntity.getId(), result.getFirst().getId());
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        MemberEntity memberEntity = MemberEntity.builder()
            .id(id)
            .build();

        RunGoalEntity entity = RunGoalEntity.builder()
            .id(id)
            .userId(memberEntity) // 널값 체크
            .totalDistance(10.0)
            .totalRunningTime(3600)
            .averagePace(300)
            .isPublic(1)
            .runCount(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        // Mock 설정
        when(runGoalRepository.findById(id)).thenReturn(Optional.of(entity));

        // 서비스 호출
        RunGoalResponseDto responseDto = runGoalService.findById(id);

        // Assertions
        assertNotNull(responseDto);
        assertEquals(id, responseDto.getId());
        assertEquals(id, responseDto.getUserId()); // id 체크
    }

    @Test
    void testFindByUserId() {
        when(runGoalRepository.findByUserId_Id(1L)).thenReturn(Collections.singletonList(runGoalEntity));

        List<RunGoalResponseDto> result = runGoalService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(runGoalEntity.getId(), result.getFirst().getId());
    }

    @Test
    void testCreateRunGoal() {
        RunGoalRequestDto requestDto = RunGoalRequestDto.builder()
            .userId(1L)
            .totalDistance(10.0)
            .totalRunningTime("00:36:00")
            .averagePace("06:00")
            .isPublic(1)
            .runCount(10)
            .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));
        when(runGoalRepository.save(any(RunGoalEntity.class))).thenReturn(runGoalEntity);

        RunGoalResponseDto result = runGoalService.createRunGoal(requestDto);

        assertNotNull(result);
        assertEquals(runGoalEntity.getId(), result.getId());
    }

    @Test
    public void testUpdateRunGoal() {
        Long id = 1L;
        Long userId = 2L;

        MemberEntity memberEntity = MemberEntity.builder()
            .id(userId)
            .build();

        RunGoalEntity existingEntity = RunGoalEntity.builder()
            .id(id)
            .userId(memberEntity)
            .totalDistance(10.0)
            .totalRunningTime(3600)
            .averagePace(300)
            .isPublic(1)
            .runCount(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        RunGoalEntity updatedEntity = RunGoalEntity.builder()
            .id(id)
            .userId(memberEntity)
            .totalDistance(15.0)
            .totalRunningTime(18000)
            .averagePace(300)
            .isPublic(1)
            .runCount(12)
            .createdAt(existingEntity.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(runGoalRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(runGoalRepository.save(any(RunGoalEntity.class))).thenReturn(updatedEntity);

        RunGoalRequestDto requestDto = RunGoalRequestDto.builder()
            .totalDistance(15.0)
            .totalRunningTime("05:00:00")
            .averagePace("05:00")
            .isPublic(1)
            .runCount(12)
            .build();

        RunGoalResponseDto responseDto = runGoalService.updateRunGoal(id, requestDto);

        assertNotNull(responseDto);
        assertEquals(15.0, responseDto.getTotalDistance());
        assertEquals(18000, responseDto.getTotalRunningTime());
        assertEquals(300, responseDto.getAveragePace());
        assertEquals(1, responseDto.getIsPublic());
        assertEquals(12, responseDto.getRunCount());
        verify(runGoalRepository, times(1)).save(any(RunGoalEntity.class));
    }



    @Test
    void testDeleteById() {
        doNothing().when(runGoalRepository).deleteById(1L);

        runGoalService.deleteById(1L);

        verify(runGoalRepository, times(1)).deleteById(1L);
    }
}
