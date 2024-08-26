package com.example.runningservice.service;

import com.example.runningservice.dto.regular_run.CrewRegularRunResponseDto;
import com.example.runningservice.dto.regular_run.RegularRunRequestDto;
import com.example.runningservice.dto.regular_run.RegularRunResponseDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.RegularRunMeetingEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        RegularRunMeetingEntity regularRunMeetingEntity = RegularRunMeetingEntity.toEntity(
            regularRunDto, crewEntity);

        regularRunMeetingRepository.save(regularRunMeetingEntity);

        return RegularRunResponseDto.fromEntity(regularRunMeetingEntity);
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
            request.getActivityRegion(), request.getTime());

        regularRunMeetingEntity.clearDayOfWeek();
        request.getDayOfWeek().forEach(regularRunMeetingEntity::addDayOfWeek);

        return RegularRunResponseDto.fromEntity(regularRunMeetingEntity);
    }

    /**
     * 크루 정기러닝 삭제
     */
    @Transactional
    public RegularRunResponseDto deleteRegularRun(Long regularId) {
        RegularRunMeetingEntity regularRunMeetingEntity = regularRunMeetingRepository
            .findById(regularId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGULAR_RUN));

        RegularRunResponseDto response = RegularRunResponseDto.fromEntity(regularRunMeetingEntity);

        regularRunMeetingRepository.delete(regularRunMeetingEntity);

        return response;
    }

    /**
     * 크루별 정기러닝 정보 조회
     */
    @Transactional
    public List<CrewRegularRunResponseDto> getRegularRunList(Pageable pageable) {
        // 갯수만큼 크루 ID를 조회하고, 크루 ID에 해당하는 모든 정기러닝 조회
        Page<CrewEntity> crewEntities = crewRepository.findAll(pageable);

        List<Long> crewIdList = crewEntities.stream().map(CrewEntity::getId).toList();
        List<RegularRunMeetingEntity> regularRunEntityList = regularRunMeetingRepository
            .findByCrewIdIn(crewIdList);

        // 크루별로 그룹화해서 보여주기 위해 크루 id를 key로 하는 Map 저장
        Map<Long, List<RegularRunResponseDto>> crewRegularMap = regularRunEntityList.stream()
            .collect(Collectors.groupingBy(
                entity -> entity.getCrew().getId(),
                Collectors.mapping(
                    RegularRunResponseDto::fromEntity,
                    Collectors.toList()
                )
            ));

        return crewRegularMap.entrySet().stream()
            .map(entry -> CrewRegularRunResponseDto.builder()
                .crewId(entry.getKey())
                .data(entry.getValue())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * 특정 크루의 정기러닝 정보 조회
     */
    public CrewRegularRunResponseDto getCrewRegularRunList(Long crewId, Pageable pageable) {
        Page<RegularRunMeetingEntity> crewEntities = regularRunMeetingRepository.findByCrew_Id(
            crewId, pageable);

        return CrewRegularRunResponseDto.builder()
            .crewId(crewId)
            .data(crewEntities.stream().map(RegularRunResponseDto::fromEntity).toList())
            .build();
    }

    /**
     * 특정 정기러닝 정보 조회
     */
    public RegularRunResponseDto getRegularRun(Long regularId) {
        RegularRunMeetingEntity regularRunMeetingEntity = regularRunMeetingRepository.findById(
            regularId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGULAR_RUN));

        return RegularRunResponseDto.fromEntity(regularRunMeetingEntity);
    }
}
