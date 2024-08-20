package com.example.runningservice.repository;

import com.example.runningservice.entity.ActivityEntity;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {

    Page<ActivityEntity> findByCrew_IdAndDateBetweenAndRegularRunIsNull(Long crewId,
        LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<ActivityEntity> findByCrew_IdAndDateBetweenAndRegularRunIsNotNull(Long crewId,
        LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<ActivityEntity> findByCrew_IdAndDateBetween(Long crewId,
        LocalDate startDate, LocalDate endDate, Pageable pageable);

    int countByCrew_Id(Long crewId);
}
