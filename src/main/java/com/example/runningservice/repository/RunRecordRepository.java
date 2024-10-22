package com.example.runningservice.repository;

import com.example.runningservice.entity.RunRecordEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRecordRepository extends JpaRepository<RunRecordEntity, Long> {
    List<RunRecordEntity> findByUserId_Id(Long userId);

    void deleteAllByUserId_Id(Long userId);

    // 특정 기간 동안의 총 거리 구하기
    @Query("SELECT SUM(r.distance) FROM runRecord r WHERE r.runningDate BETWEEN :startDate AND :endDate")
    Double findTotalDistanceBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 기간 동안의 총 러닝 시간 구하기 (초 단위)
    @Query("SELECT SUM(r.runningTime) FROM runRecord r WHERE r.runningDate BETWEEN :startDate AND :endDate")
    Integer findTotalTimeBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 기간 동안의 모든 러닝 기록 조회
    List<RunRecordEntity> findAllByRunningDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
