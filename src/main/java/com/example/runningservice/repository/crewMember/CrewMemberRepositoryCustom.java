package com.example.runningservice.repository.crewMember;

import com.example.runningservice.dto.crewMember.GetCrewMemberRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrewMemberRepositoryCustom {

    Page<CrewMemberEntity> findAllByCrewIdAndFilter(Long crewId,
        GetCrewMemberRequestDto.Filter filter, Pageable pageable);
}
