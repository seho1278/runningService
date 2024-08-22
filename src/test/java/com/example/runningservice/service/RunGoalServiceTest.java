package com.example.runningservice.service;

import com.example.runningservice.dto.runGoal.RunGoalRequestDto;
import com.example.runningservice.dto.runGoal.RunGoalResponseDto;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RunGoalServiceTest {

    @InjectMocks
    private RunGoalService runGoalService;

    @Mock
    private RunGoalRepository runGoalRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRunGoal() {
        Long userId = 1L;
        RunGoalRequestDto requestDto = RunGoalRequestDto.builder()
            .userId(userId)
            .totalDistance(100)
            .totalRunningTime("10:00:00")
            .averagePace("5:00")
            .isPublic(1)
            .runCount(10)
            .build();

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(userId);

        RunGoalEntity runGoalEntity = RunGoalEntity.builder()
            .userId(memberEntity)
            .totalDistance(requestDto.getTotalDistance())
            .totalRunningTime(requestDto.getTotalRunningTime())
            .averagePace(requestDto.getAveragePace())
            .isPublic(requestDto.getIsPublic())
            .runCount(requestDto.getRunCount())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(memberRepository.findById(userId)).thenReturn(Optional.of(memberEntity));
        when(runGoalRepository.save(any(RunGoalEntity.class))).thenReturn(runGoalEntity);

        RunGoalResponseDto responseDto = runGoalService.createRunGoal(requestDto);

        assertNotNull(responseDto);
        assertEquals(userId, responseDto.getUserId());
        assertEquals(requestDto.getTotalDistance(), responseDto.getTotalDistance());
        verify(runGoalRepository, times(1)).save(any(RunGoalEntity.class));
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        RunGoalEntity entity = RunGoalEntity.builder()
            .id(id)
            .totalDistance(100)
            .totalRunningTime("10:00:00")
            .averagePace("5:00")
            .isPublic(1)
            .runCount(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(runGoalRepository.findById(id)).thenReturn(Optional.of(entity));

        RunGoalResponseDto responseDto = runGoalService.findById(id);

        assertNotNull(responseDto);
        assertEquals(id, responseDto.getId());
    }
}
