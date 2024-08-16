package com.example.runningservice.repository;

import com.example.runningservice.entity.RegularRunMeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegularRunMeetingRepository extends JpaRepository<RegularRunMeetingEntity, Long> {

}
