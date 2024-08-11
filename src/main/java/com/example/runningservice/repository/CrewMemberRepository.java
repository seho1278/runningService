package com.example.runningservice.repository;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.JoinStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {

    int countByCrew_CrewIdAndStatus(Long crewId, JoinStatus status);

    void deleteAllByCrew_CrewId(Long crewId);
}
