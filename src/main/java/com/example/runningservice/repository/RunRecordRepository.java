package com.example.runningservice.repository;

import com.example.runningservice.entity.RunRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRecordRepository extends JpaRepository<RunRecordEntity, Long> {

    List<RunRecordEntity> findByUserId_Id(Long userId);
    //Optional<RunRecordEntity> findByRunRecordId_Id(Long runRecordId);
}
