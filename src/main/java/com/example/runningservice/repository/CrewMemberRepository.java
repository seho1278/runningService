package com.example.runningservice.repository;

import com.example.runningservice.entity.CrewMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {

}
