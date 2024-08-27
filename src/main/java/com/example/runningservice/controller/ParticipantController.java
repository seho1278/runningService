package com.example.runningservice.controller;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.activity.ParticipantResponseDto;
import com.example.runningservice.service.ParticipantService;
import com.example.runningservice.util.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * 러닝에 참석 신청한다.
     */
    @PostMapping("/crew/{crewId}/activity/{activityId}/attendance")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<ParticipantResponseDto> participateActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @PathVariable("activityId") Long activityId) {

        return ResponseEntity.ok(participantService.participateActivity(userId, activityId));
    }

    /**
     * 러닝 참석을 취소한다.
     */
    @DeleteMapping("/crew/{crewId}/activity/{activityId}/attendance")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<ParticipantResponseDto> cancelParticipateActivity(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @PathVariable("activityId") Long activityId) {

        return ResponseEntity.ok(participantService.cancelParticipateActivity(userId, activityId));
    }

    /**
     * 활동 참석자를 조회한다.
     */
    @GetMapping("/crew/{crewId}/activity/{activityId}/attendance")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<List<ParticipantResponseDto>> getActivityParticipant(
        @LoginUser Long userId,
        @PathVariable("crewId") Long crewId, @PathVariable("activityId") Long activityId,
        Pageable pageable) {

        return ResponseEntity.ok(participantService.getActivityParticipant(activityId, pageable));
    }
}
