package com.example.runningservice.service;

import com.example.runningservice.dto.regular_run.RegularRunRequestDto;
import com.example.runningservice.dto.regular_run.RegularRunResponseDto;
import com.example.runningservice.dto.regular_run.RegularRunResponseDto.Frequency;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.RegularRunMeetingEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegularRunService {

    private final CrewRepository crewRepository;
    private final RegularRunMeetingRepository regularRunMeetingRepository;

    /**
     * 크루의 정기러닝 생성
     */
    @Transactional
    public RegularRunResponseDto createRegularRun(Long crewId, RegularRunRequestDto regularRunDto) {
        CrewEntity crewEntity = crewRepository.findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));

        RegularRunMeetingEntity regularRunMeetingEntity = RegularRunMeetingEntity.builder()
            .count(regularRunDto.getCount())
            .crew(crewEntity)
            .activityRegion(regularRunDto.getActivityRegion())
            .dayOfWeek(regularRunDto.getDayOfWeek())
            .week(regularRunDto.getWeek())
            .build();

        regularRunMeetingRepository.save(regularRunMeetingEntity);

        return RegularRunResponseDto.builder()
            .id(regularRunMeetingEntity.getId())
            .frequency(Frequency.builder()
                .times(regularRunMeetingEntity.getCount())
                .weeks(regularRunMeetingEntity.getWeek())
                .build())
            .region(regularRunMeetingEntity.getActivityRegion().getRegionName())
            .weekdays(regularRunMeetingEntity.getDayOfWeek())
            .build();
    }

    /**
     * 크루 정기러닝 수정
     */
    @Transactional
    public RegularRunResponseDto updateRegularRun(Long regularId, RegularRunRequestDto request) {
        RegularRunMeetingEntity regularRunMeetingEntity = regularRunMeetingRepository
            .findById(regularId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGULAR_RUN));

        regularRunMeetingEntity.updateRegularRunInfo(request.getCount(), request.getWeek(),
            request.getActivityRegion());

        regularRunMeetingEntity.clearDayOfWeek();
        request.getDayOfWeek().forEach(regularRunMeetingEntity::addDayOfWeek);

        return RegularRunResponseDto.builder()
            .id(regularRunMeetingEntity.getId())
            .frequency(Frequency.builder()
                .times(regularRunMeetingEntity.getCount())
                .weeks(regularRunMeetingEntity.getWeek())
                .build())
            .region(regularRunMeetingEntity.getActivityRegion().getRegionName())
            .weekdays(regularRunMeetingEntity.getDayOfWeek())
            .build();
    }
}
