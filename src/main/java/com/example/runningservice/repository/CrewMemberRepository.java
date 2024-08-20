package com.example.runningservice.repository;

import com.example.runningservice.entity.CrewMemberEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {

    void deleteAllByCrew_Id(Long crewId);

    Page<CrewMemberEntity> findByMember_IdOrderByJoinedAt(Long memberId, Pageable pageable);

    Boolean existsByMember_Id(Long memberId);

    Optional<CrewMemberEntity> findByCrew_IdAndMember_Id(Long crewId, Long memberId);
}
