package com.example.runningservice.controller.post;

import com.example.runningservice.dto.post.PostRequestDto;
import com.example.runningservice.dto.post.PostResponseDto;
import com.example.runningservice.dto.post.UpdatePostRequestDto;
import com.example.runningservice.service.post.PostService;
import com.example.runningservice.util.LoginUser;
import com.example.runningservice.util.S3FileUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crew")
public class PostController {

    private final PostService postService;
    private final S3FileUtil s3FileUtil;

    @PostMapping("/{crewId}/post")
    public ResponseEntity<PostResponseDto> createPost(@LoginUser Long userId,
        @PathVariable Long crewId,
        @ModelAttribute @Valid PostRequestDto postRequestDto) {

        return ResponseEntity.ok(PostResponseDto.of(
            postService.savePost(userId, crewId, postRequestDto), s3FileUtil));
    }

    @PutMapping("/{crewId}/post")
    public ResponseEntity<PostResponseDto> updatePost(@LoginUser Long userId,
        @PathVariable("crewId") Long crewId,
        @ModelAttribute @Valid UpdatePostRequestDto requestDto) {

        return ResponseEntity.ok(
            PostResponseDto.of(postService.updatePost(userId, crewId, requestDto), s3FileUtil));
    }

}
