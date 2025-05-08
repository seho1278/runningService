package com.example.runningservice.controller;

import com.example.runningservice.dto.reference.CrewRoleResponseDto;
import com.example.runningservice.dto.reference.RegionResponseDto;
import com.example.runningservice.service.ReferenceService;
import com.example.runningservice.util.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    /**
     * 선택 가능 지역 (전국 시도) 조회
     */
    @GetMapping("/region")
    public List<RegionResponseDto> getRegion() {
        return referenceService.getRegion();
    }

    /**
     * 사용자 크루 권한 조회
     */
    @GetMapping("/crew/{crewId}/role")
    public CrewRoleResponseDto getCrewRole(@LoginUser Long loginId,
        @PathVariable("crewId") Long crewId) {
        return referenceService.getCrewRole(loginId, crewId);
    }
}
