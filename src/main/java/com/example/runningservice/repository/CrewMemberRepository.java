package com.example.runningservice.repository;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.JoinStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {

    int countByCrew_CrewIdAndStatus(Long crewId, JoinStatus status);

    void deleteAllByCrew_CrewId(Long crewId);

    Page<CrewMemberEntity> findByMember_IdAndRole(Long memberId, CrewRole role, Pageable pageable);

    Page<CrewMemberEntity> findByMember_Id(Long memberId, Pageable pageable);


    Boolean existsByMember_Id(Long memberId);

    Optional<CrewMemberEntity> findByCrew_CrewIdAndMember_Id(Long crewId, Long memberId);

    Optional<CrewMemberEntity> findByCrewAndMember(CrewEntity crew, MemberEntity member);

    Optional<CrewMemberEntity> findByCrewAndMemberAndRoleIn(CrewEntity crew, MemberEntity member, List<CrewRole> roles);
}
