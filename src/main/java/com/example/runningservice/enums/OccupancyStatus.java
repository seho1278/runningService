package com.example.runningservice.enums;

import com.example.runningservice.dto.crew.CrewFilterDto;
import com.example.runningservice.repository.CrewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public enum OccupancyStatus {

    FULL {
        @Override
        public Page<Object[]> getCrewList(CrewRepository crewRepository,
            CrewFilterDto.CrewInfo crewFilter, Pageable pageable) {
            return crewRepository.findFullCrewList(crewFilter.getActivityRegion(),
                crewFilter.getMinAge(), crewFilter.getMaxAge(), crewFilter.getGender(),
                crewFilter.getRunRecordPublic(), crewFilter.getLeaderRequired(), pageable);
        }
    },
    AVAILABLE {
        @Override
        public Page<Object[]> getCrewList(CrewRepository crewRepository,
            CrewFilterDto.CrewInfo crewFilter, Pageable pageable) {
            return crewRepository.findAvailableCrewList(crewFilter.getActivityRegion(),
                crewFilter.getMinAge(), crewFilter.getMaxAge(), crewFilter.getGender(),
                crewFilter.getRunRecordPublic(), crewFilter.getLeaderRequired(), pageable);
        }
    },
    ALL {
        @Override
        public Page<Object[]> getCrewList(CrewRepository crewRepository,
            CrewFilterDto.CrewInfo crewFilter, Pageable pageable) {
            return crewRepository.findAllCrewList(crewFilter.getActivityRegion(),
                crewFilter.getMinAge(), crewFilter.getMaxAge(), crewFilter.getGender(),
                crewFilter.getRunRecordPublic(), crewFilter.getLeaderRequired(), pageable);
        }
    };

    public abstract Page<Object[]> getCrewList(CrewRepository crewRepository,
        CrewFilterDto.CrewInfo crewFilter, Pageable pageable);
}
