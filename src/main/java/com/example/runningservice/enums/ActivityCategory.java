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
            return activityRepository.findByCrew_IdAndDateBetweenAndRegularRunIsNotNull(crewId,
                startDate, endDate, pageable).getContent();
        }

        @Override
        public List<ActivityEntity> findByCrewIdOrderByUpcomingDate(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            Pageable pageable) {
            return activityRepository.findByCrew_IdAndDateGreaterThanEqualAndRegularRunIsNotNullOrderByDate(
                crewId, startDate, pageable).getContent();
        }
    },
    ON_DEMAND {
        @Override
        public List<ActivityEntity> findByCrewIdAndDateBetween(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {
            return activityRepository.findByCrew_IdAndDateBetweenAndRegularRunIsNull(crewId,
                startDate, endDate, pageable).getContent();
        }

        @Override
        public List<ActivityEntity> findByCrewIdOrderByUpcomingDate(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            Pageable pageable) {
            return activityRepository.findByCrew_IdAndDateGreaterThanEqualAndRegularRunIsNullOrderByDate(
                crewId, startDate, pageable).getContent();
        }
    },
    ALL {
        @Override
        public List<ActivityEntity> findByCrewIdAndDateBetween(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {
            return activityRepository.findByCrew_IdAndDateBetween(crewId, startDate, endDate,
                pageable).getContent();
        }

        @Override
        public List<ActivityEntity> findByCrewIdOrderByUpcomingDate(
            ActivityRepository activityRepository, Long crewId, LocalDate startDate,
            Pageable pageable) {
            return activityRepository.findByCrew_IdAndDateGreaterThanEqualOrderByDate(
                crewId, startDate, pageable).getContent();
        }
    };

    public abstract List<ActivityEntity> findByCrewIdAndDateBetween(
        ActivityRepository activityRepository, Long crewId, LocalDate startDate, LocalDate endDate,
        Pageable pageable);

    public abstract List<ActivityEntity> findByCrewIdOrderByUpcomingDate(
        ActivityRepository activityRepository, Long crewId, LocalDate startDate, Pageable pageable);
}
