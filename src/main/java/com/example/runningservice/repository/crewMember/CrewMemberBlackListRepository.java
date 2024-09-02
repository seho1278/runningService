package com.example.runningservice.repository.crewMember;

import com.example.runningservice.entity.CrewMemberBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewMemberBlackListRepository extends JpaRepository<CrewMemberBlackListEntity, Long> {

    void deleteByCrew_Id(Long crewId);

    void deleteAllByMember_Id(Long memberId);
}
