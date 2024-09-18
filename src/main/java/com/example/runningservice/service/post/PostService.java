package com.example.runningservice.service.post;

import com.example.runningservice.dto.post.CreatePostRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import com.example.runningservice.repository.post.PostRepository;
import com.example.runningservice.util.S3FileUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3FileUtil s3FileUtil;

    @Transactional
    public PostEntity savePost(Long userId, Long crewId, CreatePostRequestDto createPostRequestDto) {
        validateCrewRole(userId, crewId, createPostRequestDto);

        createPostRequestDto.setActivityIdNullNotWithActivityReview();

        PostEntity postEntity = PostEntity.of(userId, crewId, createPostRequestDto);

        postEntity = postRepository.save(postEntity);

        postEntity.savePostImages(
            s3FileUtil.uploadFilesAndReturnFileNames("post", postEntity.getId(),
                createPostRequestDto.getImages()));

        return postEntity;
    }


    private void validateCrewRole(Long userId, Long crewId, CreatePostRequestDto createPostRequestDto) {
        //크루 회원이어야 함
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByMember_IdAndCrew_Id(userId,
                crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        //크루게시물의 공지여부(Leader, Staff 만 가능)
        if (!List.of(CrewRole.LEADER, CrewRole.STAFF).contains(crewMemberEntity.getRole())
            && createPostRequestDto.getIsNotice()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}
