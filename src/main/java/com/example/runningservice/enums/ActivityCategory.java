package com.example.runningservice.enums;

import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.repository.ActivityRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public enum ActivityCategory {
    REGULAR {
        @Override
        public List<ActivityEntity> findByCrewIdAndDateBetween(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {
            return activityRepository.findByCrew_CrewIdAndDateBetweenAndRegularRunIsNotNull(crewId,
                startDate, endDate, pageable).getContent();
        }
    },
    ON_DEMAND {
        @Override
        public List<ActivityEntity> findByCrewIdAndDateBetween(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {
            return activityRepository.findByCrew_CrewIdAndDateBetweenAndRegularRunIsNull(crewId,
                startDate, endDate, pageable).getContent();
        }
    },
    ALL {
        @Override
        public List<ActivityEntity> findByCrewIdAndDateBetween(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {
            return activityRepository.findByCrew_CrewIdAndDateBetween(crewId, startDate, endDate,
                pageable).getContent();
        }
    };

    public abstract List<ActivityEntity> findByCrewIdAndDateBetween(
        ActivityRepository activityRepository, Long crewId, LocalDate startDate, LocalDate endDate,
        Pageable pageable);
}
