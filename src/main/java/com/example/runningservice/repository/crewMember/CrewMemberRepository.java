package com.example.runningservice.repository.crewMember;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.CrewRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long>, CrewMemberRepositoryCustom {

    void deleteAllByCrew_Id(Long crewId);

    Page<CrewMemberEntity> findByMember_IdOrderByJoinedAt(Long memberId, Pageable pageable);

    Optional<CrewMemberEntity> findByMember_IdAndCrew_Id(Long memberId, Long crewId);

    Boolean existsByCrew_IdAndMember_Id(Long crewId, Long memberId);

    Optional<CrewMemberEntity> findByCrew_IdAndMember_Id(Long crewId, Long memberId);

    Optional<CrewMemberEntity> findByCrewAndMember(CrewEntity crew, MemberEntity member);

    Optional<CrewMemberEntity> findByCrewAndMemberAndRoleIn(CrewEntity crew, MemberEntity member, List<CrewRole> roles);

    List<CrewMemberEntity> findByCrew(CrewEntity crew);

    Page<CrewMemberEntity> findByCrew_IdOrderByRoleOrderAsc(Long crewId, Pageable pageable);
}
