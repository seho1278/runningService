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
    Boolean existsByMember_IdAndCrew_Id(Long memberId, Long crewId);
    Optional<JoinApplyEntity> findByIdAndStatusAndCrew_Id(Long memberId, JoinStatus status, Long crewId);
    Page<JoinApplyEntity> findAllByMember_Id(Long memberId, Pageable pageable);

    Page<JoinApplyEntity> findAllByMember_IdAndStatus(Long memberId, JoinStatus status, Pageable pageable);
    Page<JoinApplyEntity> findAllByCrew_CrewIdAndStatus(Long crewId, JoinStatus status, Pageable pageable);

    Page<JoinApplyEntity> findAllByCrew_CrewId(Long crewId, Pageable pageable);

    Optional<JoinApplyEntity> findByIdAndCrew_CrewId(Long joinApplyId, Long crewId);
}
