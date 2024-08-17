package com.example.runningservice.repository;

import com.example.runningservice.entity.RegularRunMeetingEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegularRunMeetingRepository extends JpaRepository<RegularRunMeetingEntity, Long> {

    @Query("SELECT r FROM RegularRunMeetingEntity r WHERE r.crew.crewId IN :crewIdList")
    List<RegularRunMeetingEntity> findByCrewIdIn(@Param("crewIdList") List<Long> crewIdList);

    Page<RegularRunMeetingEntity> findByCrew_CrewId(Long crewId, Pageable pageable);
}
