package com.example.runningservice.controller;

import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.dto.join.JoinApplyDto;
import com.example.runningservice.dto.join.JoinApplyDto.SimpleResponse;
import com.example.runningservice.dto.UpdateJoinApplyDto;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.service.UserJoinService;
import com.example.runningservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Slf4j
public class UserJoinController {

    private final UserJoinService userJoinService;
    private final JwtUtil jwtUtil;

    //가입신청
    @PostMapping("crew/join/apply")
    public ResponseEntity<JoinApplyDto.DetailResponse> createJoinApplication(
        @PathVariable("crew_id") Long crewId, @RequestHeader("Authorization") String token,
        @RequestBody JoinApplyDto.Request joinRequestForm) {
        return ResponseEntity.ok(userJoinService.saveJoinApply(crewId, joinRequestForm));
    }

    //가입신청내역 조회(사용자가 조회)
    @GetMapping("user/{user_id}/join/apply/list")
    public ResponseEntity<Page<SimpleResponse>> getJoinApplicaations(
        @RequestHeader("Authorization") String token, @PathVariable("user_id") Long userId,
        @RequestParam JoinStatus status, Pageable pageable) {

        GetApplicantsRequestDto joinApplicationsDto = GetApplicantsRequestDto.builder()
            .status(status)
            .pageable(pageable)
            .build();

        return ResponseEntity.ok(
            userJoinService.getJoinApplications(token, userId, joinApplicationsDto));
    }

    //가입신청내역 상세조회(사용자가 조회)
    @GetMapping("user/{user_id}/join/apply")
    public ResponseEntity<JoinApplyDto.DetailResponse> getJoinApplicationDetail(
        @PathVariable("user_id") Long userId, @RequestHeader("Authorization") String token,
        @RequestParam Long joinApplyId) {

        return ResponseEntity.ok(
            userJoinService.getJoinApplicationDetail(token, userId, joinApplyId));
    }

    //신청내역 수정
    @PutMapping("crew/join/apply")
    public ResponseEntity<JoinApplyDto.DetailResponse> updateJoinApply(
        @RequestHeader("Authorization") String token,
        @RequestBody UpdateJoinApplyDto updateJoinApplyDto) {
        return ResponseEntity.ok(
            userJoinService.updateJoinApply(token, updateJoinApplyDto));
    }

    //크루 가입신청 취소
    @DeleteMapping("/crew/join/apply")
    public ResponseEntity<String> cancelJoinApply(
        @RequestHeader("Authorization") String token, @RequestParam Long joinApplyId) {

        userJoinService.removeJoinApply(token, joinApplyId);

        String message = String.format("{} 님이 크루 가입 신청을 취소하였습니다.",
            jwtUtil.extractEmail(token.substring("Bearer ".length())));

        return ResponseEntity.ok(message);
    }
}
