package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.runRecord.RunRecordRequestDto;
import com.example.runningservice.dto.runRecord.RunRecordResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.RunGoalRepository;
import com.example.runningservice.repository.RunRecordRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RunRecordServiceTest {

    @InjectMocks
    private RunRecordService runRecordService;

    @Mock
    private RunRecordRepository runRecordRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RunGoalRepository runGoalRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        runRecordRepository = mock(RunRecordRepository.class);
        memberRepository = mock(MemberRepository.class);
        runGoalRepository = mock(RunGoalRepository.class);
        runRecordService = new RunRecordService(runRecordRepository, memberRepository, runGoalRepository);
    }

    @Test
    public void testFindByUserId() {
        Long userId = 1L;

        RunRecordEntity record1 = createMockRunRecordEntity(1L, userId, 10, 1800, 900);
        RunRecordEntity record2 = createMockRunRecordEntity(2L, userId, 10, 900, 900);
        List<RunRecordEntity> mockRecords = Arrays.asList(record1, record2);

        when(runRecordRepository.findByUserId_Id(userId)).thenReturn(mockRecords);

        List<RunRecordResponseDto> result = runRecordService.findByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userId, result.get(1).getUserId());
    }

    @Test
    public void testCreateRunRecord() {
        Long userId = 1L;
        Long goalId = 1L;
        RunRecordRequestDto requestDto = createMockRunRecordRequestDto(goalId);

        MemberEntity mockMember = MemberEntity.builder().id(userId).build();
        RunGoalEntity mockGoal = RunGoalEntity.builder().id(goalId).build();

        RunRecordEntity mockEntity = createMockRunRecordEntity(1L, userId, 10, 1800, 900);

        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMember));
        when(runGoalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));
        when(runRecordRepository.save(any(RunRecordEntity.class))).thenReturn(mockEntity);

        RunRecordResponseDto result = runRecordService.createRunRecord(userId, requestDto);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(runRecordRepository, times(1)).save(any(RunRecordEntity.class));
    }

    @Test
    public void testUpdateRunRecord_Success() {
        Long runningId = 1L;
        Long memberId = 2L;
        Long goalId = 3L;

        RunRecordRequestDto requestDto = RunRecordRequestDto.builder()
            .goalId(goalId)
            .distance(15.0)
            .runningTime("00:30:00")
            .pace("06:00")
            .isPublic(1)
            .build();

        MemberEntity memberEntity = MemberEntity.builder()
            .id(memberId)
            .build();

        RunRecordEntity existingEntity = RunRecordEntity.builder()
            .id(runningId)
            .userId(memberEntity)
            .goalId(null)
            .distance(10.0)
            .runningTime(900)
            .pace(480)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(0)
            .build();
        Map<String, Integer> map =  runRecordService.transformDTO(requestDto);
        RunRecordEntity updatedEntity = RunRecordEntity.builder()
            .id(runningId)
            .userId(memberEntity)
            .goalId(existingEntity.getGoalId())
            .distance(requestDto.getDistance())
            .runningTime(map.get("runningTime"))
            .pace(map.get("pace"))
            .createdAt(existingEntity.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isPublic(requestDto.getIsPublic())
            .build();

        when(runRecordRepository.findById(runningId)).thenReturn(Optional.of(existingEntity));
        when(runRecordRepository.save(any(RunRecordEntity.class))).thenReturn(updatedEntity);

        RunRecordResponseDto responseDto = runRecordService.updateRunRecord(runningId, requestDto);

        assertNotNull(responseDto);
        assertEquals(requestDto.getDistance(), responseDto.getDistance());
        assertEquals(map.get("runningTime"), responseDto.getRunningTime());
        assertEquals(map.get("pace"), responseDto.getPace());


        verify(runRecordRepository).save(argThat(entity ->
            entity.getId().equals(runningId) &&
                entity.getDistance().equals(requestDto.getDistance()) &&
                entity.getRunningTime().equals(map.get("runningTime")) &&
                entity.getPace().equals(map.get("pace"))
        ));
    }

    @Test
    public void testUpdateRunRecord_NotFound() {
        Long runningId = 1L;
        Long goalId = 2L;
        RunRecordRequestDto requestDto = createMockRunRecordRequestDto(goalId);

        when(runRecordRepository.findById(runningId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> runRecordService.updateRunRecord(runningId, requestDto));

        verify(runRecordRepository, never()).save(any(RunRecordEntity.class));
    }

    @Test
    public void testCalculateTotalRunRecords() {
        Long userId = 1L;

        // Mock 데이터 생성 - 누적 시간이 45분, 평균 페이스가 15분이 되도록 수정
        RunRecordEntity record1 = createMockRunRecordEntity(1L, userId, 10, 1800, 900);
        RunRecordEntity record2 = createMockRunRecordEntity(2L, userId, 10, 900, 900);
        List<RunRecordEntity> mockRecords = Arrays.asList(record1, record2);

        when(runRecordRepository.findByUserId_Id(userId)).thenReturn(mockRecords);

        RunRecordResponseDto result = runRecordService.calculateTotalRunRecords(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(20, result.getDistance()); // 총 거리 확인
        assertEquals(2700, result.getRunningTime()); // 총 러닝 시간 확인
        assertEquals(900, result.getPace()); // 평균 페이스 확인
    }

    private RunRecordEntity createMockRunRecordEntity(Long id, Long userId, double distance, int runningTime, int pace) {


        return RunRecordEntity.builder()
            .id(id)
            .userId(MemberEntity.builder().id(userId).build())
            .goalId(RunGoalEntity.builder().id(1L).build())
            .distance(distance)
            .runningTime(runningTime)
            .pace(pace)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isPublic(1)
            .build();
    }


    private RunRecordRequestDto createMockRunRecordRequestDto(Long goalId) {
        return RunRecordRequestDto.builder()
            .goalId(goalId)
            .distance(10.0)
            .runningTime("00:30:00")
            .pace("15:00")
            .isPublic(1)
            .build();
    }
}
