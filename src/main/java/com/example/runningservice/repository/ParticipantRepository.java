package com.example.runningservice.repository;

import com.example.runningservice.entity.ParticipantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

    void deleteByMember_IdAndActivity_Id(Long userId, Long activityId);

    Page<ParticipantEntity> findByActivity_Id(Long activityId, Pageable pageable);
}
