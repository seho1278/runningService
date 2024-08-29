package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.regular_run.CrewRegularRunResponseDto;
import com.example.runningservice.dto.regular_run.RegularRunRequestDto;
import com.example.runningservice.dto.regular_run.RegularRunResponseDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.RegularRunMeetingEntity;
import com.example.runningservice.enums.Region;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class RegularRunServiceTest {

    @Mock
    private CrewRepository crewRepository;
    @Mock
    private RegularRunMeetingRepository regularRunMeetingRepository;
    @Mock
    private RegularRunRequestDto regularRunDto;
    @InjectMocks
    private RegularRunService regularRunService;

    @Test
    @DisplayName("정기러닝 생성")
    void createRegularRun() {
        Long crewId = 1L;

        when(regularRunDto.getCount()).thenReturn(2);
        when(regularRunDto.getWeek()).thenReturn(3);
        when(regularRunDto.getDayOfWeek()).thenReturn(List.of("월요일", "화요일"));
        when(regularRunDto.getTime()).thenReturn(LocalTime.of(1,1));
        when(regularRunDto.getActivityRegion()).thenReturn(Region.SEOUL);
        CrewEntity mockCrewEntity = new CrewEntity();
        given(crewRepository.findById(crewId)).willReturn(Optional.of(mockCrewEntity));

        // When
        RegularRunResponseDto response = regularRunService.createRegularRun(crewId, regularRunDto);

        // Then
        verify(regularRunMeetingRepository).save(any(RegularRunMeetingEntity.class));
        assertNotNull(response);
        assertEquals(2, response.getCount());
        assertEquals(3, response.getWeek());
        assertEquals(Region.SEOUL.getRegionName(), response.getActivityRegion());
        assertEquals(2, response.getDayOfWeek().size());
    }

    @Test
    @DisplayName("정기러닝 수정")
    void updateRegularRun() {
        Long regularId = 1L;

        when(regularRunDto.getCount()).thenReturn(2);
        when(regularRunDto.getWeek()).thenReturn(3);
        when(regularRunDto.getDayOfWeek()).thenReturn(List.of("월요일", "화요일"));
        when(regularRunDto.getActivityRegion()).thenReturn(Region.SEOUL);

        RegularRunMeetingEntity regularEntity = new RegularRunMeetingEntity();
        ReflectionTestUtils.setField(regularEntity, "dayOfWeek",
            new ArrayList<>(List.of("월요일", "화요일")));

        when(regularRunMeetingRepository.findById(regularId)).thenReturn(
            Optional.of(regularEntity));

        // When
        RegularRunResponseDto response = regularRunService.updateRegularRun(regularId,
            regularRunDto);

        // Then
        assertNotNull(response);
        assertEquals(regularRunDto.getActivityRegion().getRegionName(), response.getActivityRegion());
        assertEquals(List.of("월요일", "화요일"), response.getDayOfWeek());
    }

    @Test
    @DisplayName("정기러닝 삭제")
    void deleteRegularRun() {
        Long regularId = 1L;

        RegularRunMeetingEntity mockRegularRunMeetingEntity = mock(RegularRunMeetingEntity.class);
        when(mockRegularRunMeetingEntity.getActivityRegion()).thenReturn(Region.SEOUL);
        when(mockRegularRunMeetingEntity.getId()).thenReturn(regularId);

        when(regularRunMeetingRepository.findById(regularId)).thenReturn(
            Optional.of(mockRegularRunMeetingEntity));

        // When
        RegularRunResponseDto response = regularRunService.deleteRegularRun(regularId);

        // Then
        verify(regularRunMeetingRepository).delete(mockRegularRunMeetingEntity);
        assertNotNull(response);
        assertEquals(regularId, response.getId());
        assertEquals(mockRegularRunMeetingEntity.getActivityRegion().getRegionName(),
            response.getActivityRegion());
    }

    @Test
    @DisplayName("크루별 정기러닝 정보 조회")
    void getRegularRunList() {
        Pageable pageable = PageRequest.of(0, 2);

        List<Long> crewIdList = List.of(1L, 2L);
        List<CrewEntity> crewList = List.of(CrewEntity.builder().id(crewIdList.get(0)).build(),
            CrewEntity.builder().id(crewIdList.get(1)).build());
        Page<CrewEntity> crewPage = new PageImpl<>(crewList);

        given(crewRepository.findAll(pageable)).willReturn(crewPage);

        RegularRunMeetingEntity mockRegular1 = mock(RegularRunMeetingEntity.class);
        when(mockRegular1.getActivityRegion()).thenReturn(Region.SEOUL);
        when(mockRegular1.getCrew()).thenReturn(crewList.get(0));

        RegularRunMeetingEntity mockRegular2 = mock(RegularRunMeetingEntity.class);
        when(mockRegular2.getActivityRegion()).thenReturn(Region.BUSAN);
        when(mockRegular2.getCrew()).thenReturn(crewList.get(1));

        List<RegularRunMeetingEntity> regularList = List.of(mockRegular1, mockRegular2);

        given(regularRunMeetingRepository.findByCrewIdIn(crewIdList)).willReturn(regularList);

        // When
        List<CrewRegularRunResponseDto> response = regularRunService.getRegularRunList(pageable);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(crewIdList.get(0), response.get(0).getCrewId());
        assertEquals(1, response.get(0).getData().size());
        assertEquals(Region.SEOUL.getRegionName(), response.get(0).getData().get(0).getActivityRegion());
    }

    @Test
    @DisplayName("특정 크루의 정기러닝 정보 조회")
    void getCrewRegularRunList() {
        // given
        Long crewId = 1L;
        RegularRunMeetingEntity entity = RegularRunMeetingEntity.builder()
            .activityRegion(Region.BUSAN)
            .dayOfWeek(List.of())
            .build();
        Page<RegularRunMeetingEntity> page = new PageImpl<>(List.of(entity));

        given(regularRunMeetingRepository.findByCrew_Id(crewId, Pageable.unpaged()))
            .willReturn(page);

        // when
        CrewRegularRunResponseDto result = regularRunService.getCrewRegularRunList(crewId,
            Pageable.unpaged());

        // then
        assertNotNull(result);
        assertEquals(crewId, result.getCrewId());
        assertEquals(1, result.getData().size());
        verify(regularRunMeetingRepository).findByCrew_Id(crewId, Pageable.unpaged());
    }

    @Test
    @DisplayName("특정 정기러닝 정보 조회")
    void getRegularRun() {
        // Given
        Long regularId = 1L;
        RegularRunMeetingEntity entity = RegularRunMeetingEntity.builder()
            .activityRegion(Region.BUSAN)
            .dayOfWeek(List.of())
            .build();
        RegularRunResponseDto expectedDto = RegularRunResponseDto.fromEntity(entity);

        given(regularRunMeetingRepository.findById(regularId)).willReturn(Optional.of(entity));

        // When
        RegularRunResponseDto result = regularRunService.getRegularRun(regularId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getActivityRegion(), result.getActivityRegion());
        assertEquals(expectedDto.getDayOfWeek(), result.getDayOfWeek());
        verify(regularRunMeetingRepository).findById(regularId);
    }
}