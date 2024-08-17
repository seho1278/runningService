package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.activity.ActivityRequestDto;
import com.example.runningservice.dto.activity.ActivityResponseDto;
import com.example.runningservice.service.ActivityService;
import com.example.runningservice.util.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * (정기/번개)러닝 일정 생성
     */
    @PostMapping("/crew/{crewId}/activity")
    @CrewRoleCheck(role = {"LEADER", "MEMBER", "STAFF"})
    public ResponseEntity<ActivityResponseDto> createActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @RequestBody ActivityRequestDto.Create request) {

        return ResponseEntity.ok(activityService.createActivity(userId, crewId, request));
    }
}
