package com.example.runningservice.repository;

import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.enums.JoinStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinApplicationRepository extends JpaRepository<JoinApplyEntity, Long> {
    Optional<JoinApplyEntity> findByIdAndMember_Id(Long id, Long memberId);
    Optional<JoinApplyEntity> findByIdAndStatus(Long id, JoinStatus status);
    Optional<JoinApplyEntity> findByIdAndStatusAndCrew_Id(Long memberId, JoinStatus status, Long crewId);
    Page<JoinApplyEntity> findAllByMember_Id(Long memberId, Pageable pageable);

    Page<JoinApplyEntity> findAllByMember_IdAndStatus(Long memberId, JoinStatus status, Pageable pageable);
    Page<JoinApplyEntity> findAllByCrew_IdAndStatus(Long crewId, JoinStatus status, Pageable pageable);

    Page<JoinApplyEntity> findAllByCrew_Id(Long crewId, Pageable pageable);

    Optional<JoinApplyEntity> findByIdAndCrew_Id(Long joinApplyId, Long crewId);

    Optional<JoinApplyEntity> findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(Long userId, Long crewId);

    void deleteByCrew_Id(Long crewId);
}
