package com.example.runningservice.controller;

import com.example.runningservice.dto.UpdateJoinApplyDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.dto.join.JoinApplyDto;
import com.example.runningservice.dto.join.JoinApplyDto.SimpleResponse;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.service.UserJoinService;
import com.example.runningservice.util.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@Slf4j
public class UserJoinController {

    private final UserJoinService userJoinService;

    /**
     * 가입신청
     */
    @PostMapping("crew/{crew_id}/join/apply")
    public ResponseEntity<JoinApplyDto.DetailResponse> createJoinApplication(
        @LoginUser Long userId, @PathVariable("crew_id") Long crewId,
        @RequestBody JoinApplyDto.Request joinRequestForm) {
        return ResponseEntity.ok(userJoinService.saveJoinApply(crewId, userId, joinRequestForm));
    }

    /**
     * 사용자가 자신의 가입신청 리스트 조회
     */
    @GetMapping("user/join/apply/list")
    public ResponseEntity<Page<SimpleResponse>> getJoinApplications(
        @LoginUser Long userId, @RequestParam(required = false) JoinStatus status,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        GetApplicantsRequestDto joinApplicationsDto = GetApplicantsRequestDto.builder()
                .status(status)
                .pageable(pageable)
                .build();

            return ResponseEntity.ok(
                userJoinService.getJoinApplications(userId, joinApplicationsDto));
        }

        /**
         *  사용자가 자신의 가입신청내역 상세 조회
         */
        @GetMapping("user/join/apply")
        public ResponseEntity<JoinApplyDto.DetailResponse> getJoinApplicationDetail (
            @LoginUser Long userId, @RequestParam Long joinApplyId){

            return ResponseEntity.ok(
                userJoinService.getJoinApplicationDetail(userId, joinApplyId));
        }

        /**
         * 가입신청 수정
         */
        //신청내역 수정
        @PutMapping("crew/join/apply")
        public ResponseEntity<JoinApplyDto.DetailResponse> updateJoinApply (
            @LoginUser Long userId,
            @RequestBody UpdateJoinApplyDto updateJoinApplyDto){
            return ResponseEntity.ok(
                userJoinService.updateJoinApply(userId, updateJoinApplyDto));
        }

        /**
         * 가입신청 취소
         */
        //크루 가입신청 취소
        @DeleteMapping("/crew/join/apply")
        public ResponseEntity<Void> cancelJoinApply (
            @LoginUser Long userId, @RequestParam Long joinApplyId){

            userJoinService.removeJoinApply(userId, joinApplyId);
            return ResponseEntity.ok().build();
        }
    }
