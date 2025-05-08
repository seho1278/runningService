package com.example.runningservice.repository;

import com.example.runningservice.entity.RunGoalEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunGoalRepository extends JpaRepository<RunGoalEntity, Long> {
    List<RunGoalEntity> findByUserId_Id(Long userId);

    void deleteAllByUserId_Id(Long userId);

    Optional<RunGoalEntity> findFirstByUserId_IdOrderByCreatedAtDesc(Long userId);

}
