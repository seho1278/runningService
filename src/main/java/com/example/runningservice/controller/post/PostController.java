package com.example.runningservice.controller.post;

import com.example.runningservice.aop.CrewRoleCheck;
import com.example.runningservice.dto.post.GetPostDetailResponseDto;
import com.example.runningservice.dto.post.GetPostRequestDto;
import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.post.PostRequestDto;
import com.example.runningservice.dto.post.PostResponseDto;
import com.example.runningservice.dto.post.UpdatePostRequestDto;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.service.post.PostService;
import com.example.runningservice.util.LoginUser;
import com.example.runningservice.util.S3FileUtil;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crew")
@Slf4j
public class PostController {

    private final PostService postService;
    private final S3FileUtil s3FileUtil;

    private static final int MAX_POST_IDS = 20;  // 삭제할 수 있는 최대 게시물 수

    /**
     * 게시물 생성
     */
    @PostMapping("/{crewId}/post")
    public ResponseEntity<PostResponseDto> createPost(@LoginUser Long userId,
        @PathVariable Long crewId,
        @ModelAttribute @Valid PostRequestDto postRequestDto) {

        return ResponseEntity.ok(PostResponseDto.of(
            postService.savePost(userId, crewId, postRequestDto), s3FileUtil));
    }

    /**
     * 게시물 수정
     */
    @PutMapping("/{crewId}/post")
    public ResponseEntity<PostResponseDto> updatePost(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId,
        @ModelAttribute @Valid UpdatePostRequestDto requestDto) {

        return ResponseEntity.ok(
            PostResponseDto.of(postService.updatePost(userId, crewId, requestDto), s3FileUtil));
    }

    /**
     * 게시물 목록조회
     */
    @GetMapping("/{crewId}/posts")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<Page<GetPostSimpleResponseDto>> getPosts(@LoginUser Long userId,
        @PathVariable Long crewId,
        @PageableDefault(page = 0, size = 10, direction = Direction.DESC, sort = "createdAt") Pageable pageable,
        @ModelAttribute
        GetPostRequestDto.Filter filter) {

        // 공지사항 가져오기
        Page<PostEntity> noticePosts = postService.getNoticePost(crewId);

        // 일반 게시물 가져오기, 공지사항 수만큼 페이지 사이즈 조정
        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(),
            pageable.getPageSize() - noticePosts.getSize(), pageable.getSort());
        Page<PostEntity> posts = postService.getPosts(crewId, adjustedPageable, filter);

        // 공지사항과 일반 게시물 합치기
        List<GetPostSimpleResponseDto> combinedPosts = new ArrayList<>();
        combinedPosts.addAll(noticePosts.stream().map(GetPostSimpleResponseDto::of).toList());
        combinedPosts.addAll(posts.stream().map(GetPostSimpleResponseDto::of).toList());

        // 페이지 정보를 유지하기 위해 PageImpl을 사용하여 반환
        Page<GetPostSimpleResponseDto> resultPage = new PageImpl<>(combinedPosts, pageable,
            posts.getTotalElements() + noticePosts.getTotalElements());

        return ResponseEntity.ok(resultPage);
    }

    /**
     * 게시물 상세조회
     */
    @GetMapping("/{crewId}/post")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<GetPostDetailResponseDto> getPost(@LoginUser Long userID,
        @PathVariable Long crewId,
        @RequestParam Long postId) {

        PostEntity post = postService.getPost(postId);
        log.info("post: {}", post);

        return ResponseEntity.ok(
            GetPostDetailResponseDto.of(post, s3FileUtil));
    }

    /**
     * 나의 게시물 삭제
     */
    @DeleteMapping("/{crewId}/post")
    @CrewRoleCheck(role = {"LEADER", "STAFF", "MEMBER"})
    public ResponseEntity<Void> deletePost(@LoginUser Long userId, @PathVariable Long crewId,
        @RequestParam Long postId) {
        postService.deleteMyPost(userId, postId);
        return ResponseEntity.ok().build();
    }

    /**
     * 여러 게시물 삭제(운영진의 게시판 관리)
     */
    @DeleteMapping("/{crewId}/posts")
    @CrewRoleCheck(role = {"LEADER", "STAFF"})
    public ResponseEntity<Void> deletePosts(@LoginUser Long userId, @PathVariable Long crewId,
        @RequestParam List<Long> postIds) {

        if (postIds.size() > MAX_POST_IDS) {
            throw new CustomException(ErrorCode.MAX_DELETE_SIZE_OVER);
        }

        postService.deletePosts(postIds);
        return ResponseEntity.ok().build();
    }
}
