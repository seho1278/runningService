package com.example.runningservice.repository;

import com.example.runningservice.entity.ActivityEntity;
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
        + "AND a.crew.id = :crewId "
        + "AND a.regularRun IS NULL")
    Page<ActivityEntity> findByCrew_IdAndDateBetweenAndRegularRunIsNull(
        @Param("crewId") Long crewId,
        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
        Pageable pageable);

    @Query("SELECT a "
        + "FROM ActivityEntity a "
        + "WHERE (:startDate IS NULL OR a.date >= :startDate) "
        + "AND (:endDate IS NULL OR a.date <= :endDate) "
        + "AND a.crew.id = :crewId "
        + "AND a.regularRun IS NOT NULL")
    Page<ActivityEntity> findByCrew_IdAndDateBetweenAndRegularRunIsNotNull(
        @Param("crewId") Long crewId,
        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
        Pageable pageable);

    @Query("SELECT a "
        + "FROM ActivityEntity a "
        + "WHERE (:startDate IS NULL OR a.date >= :startDate) "
        + "AND (:endDate IS NULL OR a.date <= :endDate) "
        + "AND a.crew.id = :crewId")
    Page<ActivityEntity> findByCrew_IdAndDateBetween(
        @Param("crewId") Long crewId,
        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
        Pageable pageable);

    Page<ActivityEntity> findByCrew_IdAndDateGreaterThanEqualAndRegularRunIsNullOrderByDate(
        Long crewId, LocalDate startDate, Pageable pageable);

    Page<ActivityEntity> findByCrew_IdAndDateGreaterThanEqualAndRegularRunIsNotNullOrderByDate(
        Long crewId, LocalDate startDate, Pageable pageable);

    Page<ActivityEntity> findByCrew_IdAndDateGreaterThanEqualOrderByDate(Long crewId,
        LocalDate startDate, Pageable pageable);

    int countByCrew_Id(Long crewId);

    void deleteByCrew_Id(Long crewId);
}
