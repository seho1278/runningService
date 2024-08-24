package com.example.runningservice.service;

import com.example.runningservice.dto.reference.CrewRoleResponseDto;
import com.example.runningservice.dto.reference.RegionResponseDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.Region;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final CrewMemberRepository crewMemberRepository;

    /**
     * 선택 가능 지역 (전국 시도) 조회
     */
    public List<RegionResponseDto> getRegion() {
        List<RegionResponseDto> response = new ArrayList<>();

        for (Region region : Region.values()) {
            response.add(new RegionResponseDto(region.name(), region.getRegionName()));
        }

        return response;
    }

    /**
     * 사용자 크루 권한 조회
     */
    public CrewRoleResponseDto getCrewRole(Long userId, Long crewId) {
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByCrew_IdAndMember_Id(
            crewId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_CREW_ACCESS));

        return new CrewRoleResponseDto(crewId, crewMemberEntity.getRole());
    }
}
