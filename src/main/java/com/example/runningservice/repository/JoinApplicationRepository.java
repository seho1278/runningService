package com.example.runningservice.repository;

import com.example.runningservice.entity.JoinApplyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinApplicationRepository extends JpaRepository<JoinApplyEntity, Long> {
    List<JoinApplyEntity> findAllByMember_Id(Long memberId);
    Optional<JoinApplyEntity> findByIdAndMember_Email(Long id, String email);
    Optional<JoinApplyEntity> findByIdAndMember_Id(Long id, Long memberId);
}
