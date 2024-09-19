package com.example.runningservice.service.post;

import com.example.runningservice.dto.post.PostRequestDto;
import com.example.runningservice.dto.post.UpdatePostRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import com.example.runningservice.repository.post.PostRepository;
import com.example.runningservice.util.S3FileUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public PostEntity savePost(Long userId, Long crewId, PostRequestDto postRequestDto) {
        validateCrewRole(userId, crewId, postRequestDto);

        postRequestDto.setActivityIdNullNotWithActivityReview();

        PostEntity postEntity = PostEntity.of(userId, crewId, postRequestDto);

        postEntity = postRepository.save(postEntity);

        postEntity.addPostImages(
            s3FileUtil.uploadFilesAndReturnFileNames("post", postEntity.getId(),
                postRequestDto.getImagesToUpload()));

        return postEntity;
    }

    @Transactional
    public PostEntity updatePost(Long userId, Long crewId, UpdatePostRequestDto requestDto) {
        validateCrewRole(userId, crewId, requestDto);

        requestDto.setActivityIdNullNotWithActivityReview();

        PostEntity postEntity = postRepository.findByIdAndMemberId(requestDto.getPostId(), userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        postEntity.updatePost(requestDto);

        //저체삭제 요청일 때
        if (Boolean.TRUE.equals(requestDto.getDeleteAllImages())) {
            List<String> images = new ArrayList<>(postEntity.getImages());
            images.forEach(s3FileUtil::deleteObject);
            images.clear();
            postEntity.savePostImages(images);
        } else if (requestDto.getImagesToDelete() != null) { //일부 이미지 delete 요청
            Set<String> images = new HashSet<>(postEntity.getImages());
            requestDto.getImagesToDelete().forEach(e -> {
                s3FileUtil.deleteObject(e);
                images.remove(e);
            });
            postEntity.savePostImages(images);
        }
        //일부 이미지 추가 요청
        if (requestDto.getImagesToUpload() != null) {
            postEntity.addPostImages(s3FileUtil.uploadFilesAndReturnFileNames("post",
                requestDto.getPostId(), requestDto.getImagesToUpload()));
        }

        return postEntity;
    }


    private void validateCrewRole(Long userId, Long crewId, PostRequestDto postRequestDto) {
        //크루 회원이어야 함
        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByMember_IdAndCrew_Id(userId,
                crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        //크루게시물의 공지여부(Leader, Staff 만 가능)
        if (!List.of(CrewRole.LEADER, CrewRole.STAFF).contains(crewMemberEntity.getRole())
            && postRequestDto.getIsNotice()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}
