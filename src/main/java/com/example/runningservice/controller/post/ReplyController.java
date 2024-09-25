package com.example.runningservice.controller.post;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.post.reply.CreateReplyRequestDto;
import com.example.runningservice.dto.post.reply.ReplyResponseDto;
import com.example.runningservice.dto.post.reply.UpdateReplyRequestDto;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.service.post.ReplyService;
import com.example.runningservice.util.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
public class ReplyController {

    private final ReplyService replyService;
    private final static int REPLY_ID_SIZE = 20;

    /**
     * 댓글 작성(저장)
     */
    @PostMapping("/crew/{crewId}/post/reply")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<ReplyResponseDto> createReply(@LoginUser Long userId,
        @PathVariable Long crewId, @RequestBody CreateReplyRequestDto requestDto) {
        return ResponseEntity.ok(ReplyResponseDto.of(replyService.writeReply(userId, requestDto)));
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/crew/{crewId}/post/reply")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<?> updateReply(@LoginUser Long userId, @PathVariable Long crewId,
        @RequestBody UpdateReplyRequestDto requestDto) {
        return ResponseEntity.ok(ReplyResponseDto.of(replyService.updateReply(userId, requestDto)));
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("/crew/{crewId}/post/{postId}/replies")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<Page<ReplyResponseDto>> readReplies(@LoginUser Long userId,
        @PathVariable Long crewId,
        @PathVariable Long postId,
        @PageableDefault(page = 0, size = 10, direction = Direction.DESC, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
            replyService.getReplies(postId, pageable).map(ReplyResponseDto::of));
    }

    /**
     * 내가 작성한 댓글 조회
     */
    @GetMapping("/me/replies")
    public ResponseEntity<Page<ReplyResponseDto>> readMyReplies(@LoginUser Long userId,
        @PageableDefault(page = 0, size = 10, direction = Direction.DESC, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(
            replyService.getMyReplies(userId, pageable).map(ReplyResponseDto::of));
    }

    /**
     * 댓글 삭제(내가 작성한 댓글)
     */
    @DeleteMapping("/crew/{crewId}/post/reply")
    public ResponseEntity<?> deleteReply(@LoginUser Long userId, @PathVariable Long crewId,
        @RequestParam Long replyId) {

        replyService.deleteReply(userId, replyId);
        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 삭제(크루 내 댓글 삭제. 운영진 권한)
     */
    @DeleteMapping("/crew/{crewId}/post/replies")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<?> deleteReplies(@LoginUser Long userId, @PathVariable Long crewId,
        @RequestParam List<Long> replyIds) {

        if (replyIds.size() > REPLY_ID_SIZE) {
            throw new CustomException(ErrorCode.MAX_DELETE_SIZE_OVER);
        }
        replyService.deleteReplies(replyIds);
        return ResponseEntity.ok().build();
    }
}

