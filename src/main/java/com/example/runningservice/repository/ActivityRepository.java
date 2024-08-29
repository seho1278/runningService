package com.example.runningservice.repository;

import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.enums.ActivityCategory;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {

    @Query("SELECT a "
        + "FROM ActivityEntity a "
        + "WHERE (:startDate IS NULL OR a.date >= :startDate) "
        + "AND (:endDate IS NULL OR a.date <= :endDate) "
        + "AND (:category IS NULL OR a.category = :category)"
        + "AND a.crew.id = :crewId ")
    Page<ActivityEntity> findByCrewIdAndCategoryAndDateBetween(
        @Param("crewId") Long crewId, @Param("category") ActivityCategory category,
        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
        Pageable pageable);

    Page<ActivityEntity> findByCrew_IdAndCategoryAndDateGreaterThanEqualOrderByDate(
        Long crewId, ActivityCategory category, LocalDate startDate, Pageable pageable);

    int countByCrew_Id(Long crewId);

    void deleteByCrew_Id(Long crewId);
}
