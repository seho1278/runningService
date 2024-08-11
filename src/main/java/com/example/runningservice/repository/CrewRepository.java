package com.example.runningservice.repository;

import com.example.runningservice.entity.CrewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewRepository extends JpaRepository<CrewEntity, Long> {

}
