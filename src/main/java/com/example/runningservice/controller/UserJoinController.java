package com.example.runningservice.controller;

import com.example.runningservice.dto.JoinApplyDto;
import com.example.runningservice.dto.JoinApplyDto.SimpleResponse;
import com.example.runningservice.dto.UpdateJoinRequestDto;
import com.example.runningservice.service.UserJoinService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserJoinController {

    private final UserJoinService userJoinService;

    //가입신청
    @PostMapping("crew/{crew_id}/join/apply")
    public ResponseEntity<JoinApplyDto.DetailResponse> createJoinApplication(
        @PathVariable("crew_id") Long crewId, @RequestHeader("Authorization") String token,
        @RequestBody JoinApplyDto.Request joinRequestForm) {
        return ResponseEntity.ok(userJoinService.saveJoinApply(crewId, joinRequestForm));
    }

    //가입신청내역 조회(사용자가 조회)
    @GetMapping("user/{user_id}/join/apply/list")
    public ResponseEntity<List<SimpleResponse>> getJoinApplicaations(
        @RequestHeader("Authorization") String token, @PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(userJoinService.getJoinApplications(token, userId));
    }

    //가입신청내역 상세조회(사용자가 조회)
    @GetMapping("user/{user_id}/join/apply")
    public ResponseEntity<?> getJoinApplicationDetail(@PathVariable("user_id") Long userId,
        @RequestHeader("Authorization") String token, @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(
            userJoinService.getJoinApplicationDetail(token, userId, joinApplyId));
    }

    //아래 내용은 다음 PR에서 작성
    //신청내역 수정
    @PutMapping("user/{user_id}/join/apply")
    public ResponseEntity<?> updateJoinRequest(@RequestParam Long crewId,
        @RequestHeader("Authorization") String token,
        @RequestBody UpdateJoinRequestDto updateJoinRequestDto) {
        return null;
    }

    //크루 탈퇴
    @DeleteMapping("/crew/{crew_id}/leave")
    public ResponseEntity<?> leaveCrew(@PathVariable("crew_id") String crewId,
        @RequestHeader("Authorization") String token) {
        return null;
    }
}
