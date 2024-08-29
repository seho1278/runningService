package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.activity.ActivityFilterDto;
import com.example.runningservice.dto.activity.ActivityRequestDto;
import com.example.runningservice.dto.activity.ActivityResponseDto;
import com.example.runningservice.enums.ActivityCategory;
import com.example.runningservice.service.ActivityService;
import com.example.runningservice.util.LoginUser;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

        if (request.getCategory().equals(ActivityCategory.REGULAR)) {
            return ResponseEntity.ok(
                activityService.createRegularActivity(userId, crewId, request));
        } else { // ON_DEMAND (번개)
            return ResponseEntity.ok(
                activityService.createOnDemandActivity(userId, crewId, request));
        }
    }

    /**
     * (정기/번개)러닝 일정 수정
     */
    @PutMapping("/crew/{crewId}/activity/{activityId}")
    @CrewRoleCheck(role = {"LEADER", "MEMBER", "STAFF"})
    public ResponseEntity<ActivityResponseDto> updateActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @PathVariable("activityId") Long activityId,
        @RequestBody ActivityRequestDto.Update request) {

        return ResponseEntity.ok(
            activityService.updateActivity(userId, crewId, activityId, request));
    }

    /**
     * (정기/번개)러닝 일정 삭제
     */
    @DeleteMapping("/crew/{crewId}/activity/{activityId}")
    @CrewRoleCheck(role = {"LEADER", "MEMBER", "STAFF"})
    public ResponseEntity<ActivityResponseDto> deleteActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @PathVariable("activityId") Long activityId) {

        return ResponseEntity.ok(activityService.deleteActivity(userId, crewId, activityId));
    }

    /**
     * 특정 (정기/번개)러닝 일정 조회
     */
    @GetMapping("/crew/{crewId}/activity/{activityId}")
    @CrewRoleCheck(role = {"LEADER", "MEMBER", "STAFF"})
    public ResponseEntity<ActivityResponseDto> getActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @PathVariable("activityId") Long activityId) {

        return ResponseEntity.ok(activityService.getActivity(activityId));
    }

    /**
     * 크루 (정기/번개)러닝 날짜 구간별 일정 조회
     */
    @GetMapping("/crew/{crewId}/activity/date")
    @CrewRoleCheck(role = {"LEADER", "MEMBER", "STAFF"})
    public ResponseEntity<List<ActivityResponseDto>> getCrewActivityByDate(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, Pageable pageable,
        @RequestParam(value = "startDate", required = false) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) LocalDate endDate,
        @RequestParam(value = "category", required = false) ActivityCategory category) {

        return ResponseEntity.ok(activityService.getCrewActivityByDate(crewId,
            ActivityFilterDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .category(category)
                .build(), pageable));
    }

    /**
     * 다가오는 크루 (정기/번개)러닝 일정 조회
     */
    @GetMapping("/crew/{crewId}/activity")
    public ResponseEntity<List<ActivityResponseDto>> getCrewActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, Pageable pageable,
        @RequestParam(value = "category", required = false) ActivityCategory category) {

        return ResponseEntity.ok(activityService.getCrewActivity(crewId, category, pageable));
    }
}
