package com.example.runningservice.repository.crew;


import com.example.runningservice.entity.CrewEntity;
import java.util.Optional;

public interface CrewRepositoryCustom {
    Optional<CrewEntity> findByIdAndMemberCountLessThanCapacity(Long crewId);
}
