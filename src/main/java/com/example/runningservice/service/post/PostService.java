package com.example.runningservice.service.post;

import com.example.runningservice.dto.post.GetPostRequestDto;
import com.example.runningservice.dto.post.PostRequestDto;
import com.example.runningservice.dto.post.UpdatePostRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import com.example.runningservice.repository.post.PostRepository;
import com.example.runningservice.util.S3FileUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final S3FileUtil s3FileUtil;

    private static final List<String> ALLOWED_SORT_FIELD = new ArrayList<>(List.of("createdAt"));

    @Transactional
    public PostEntity savePost(Long userId, Long crewId, PostRequestDto postRequestDto) {
        validateCrewRole(userId, crewId, postRequestDto);

        //활동후기가 아닌 경우 activityId를 null로 저장
        postRequestDto.setActivityIdNullNotWithActivityReview();

        MemberEntity memberEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        PostEntity postEntity = PostEntity.of(crewId, postRequestDto, memberEntity);

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

        PostEntity postEntity = postRepository.findByIdAndMember_Id(requestDto.getPostId(), userId)
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

    @Transactional
    public Page<PostEntity> getPosts(Long crewId, Pageable pageable,
        GetPostRequestDto.Filter filter) {

        //정렬기준 검증(정해진 정렬기준만 요청 가능)
        validateSort(pageable);

        //필터값 반영하여 공지사항이 아닌 게시글 가져오기
        return postRepository.findAllNotNoticeByCrewIdAndFilter(crewId, filter, pageable);
    }

    @Transactional
    public Page<PostEntity> getNoticePost(Long crewId) {
        Pageable topFive = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        return postRepository.findAllByCrewIdAndIsNoticeOrderByCreatedAtDesc(
            crewId, true, topFive);
    }

    @Transactional
    public PostEntity getPost(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
    }

    @Transactional
    public void deleteMyPost(Long userId, Long postId) {
        PostEntity postEntity = postRepository.findByIdAndMember_Id(postId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        postRepository.delete(postEntity);
    }

    @Transactional
    public void deletePosts(List<Long> postIds) {
        List<PostEntity> postEntities = postRepository.findAllById(postIds);

        if (postEntities.size() != postIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SOME_POST);
        }

        postRepository.deleteAllById(postIds);
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

    private void validateSort(Pageable pageable) {
        pageable.getSort().stream().forEach(order -> {
            if (!ALLOWED_SORT_FIELD.contains(order.getProperty())) {
                throw new CustomException(ErrorCode.INVALID_SORT);
            }
        });
    }
}
