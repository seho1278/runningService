package com.example.runningservice.service.post;

import com.example.runningservice.dto.post.reply.CreateReplyRequestDto;
import com.example.runningservice.dto.post.reply.UpdateReplyRequestDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.entity.post.ReplyEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.post.PostRepository;
import com.example.runningservice.repository.post.ReplyRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;

    private static final List<String> ALLOWED_SORT_FIELD = new ArrayList<>(List.of("createdAt"));

    @Transactional
    public ReplyEntity writeReply(Long userId, CreateReplyRequestDto requestDto) {
        PostEntity postEntity = postRepository.findById(requestDto.getPostId())
            .orElseThrow(() -> new CustomException(
                ErrorCode.NOT_FOUND_POST));

        MemberEntity memberEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return replyRepository.save(
            ReplyEntity.of(postEntity, memberEntity, requestDto.getContent()));
    }

    @Transactional
    public ReplyEntity updateReply(Long userId, UpdateReplyRequestDto requestDto) {
        ReplyEntity replyEntity = replyRepository.findByIdAndMember_Id(requestDto.getReplyId(),
            userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPLY));

        replyEntity.updateContent(requestDto.getContent());
        return replyEntity;
    }

    @Transactional(readOnly = true)
    public Page<ReplyEntity> getReplies(Long postId, Pageable pageable) {

        validateSortField(pageable);

        return replyRepository.findAllByPost_Id(postId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReplyEntity> getMyReplies(Long userId, Pageable pageable) {
        validateSortField(pageable);
        return replyRepository.findAllByMember_Id(userId, pageable);

    }

    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        ReplyEntity replyEntity = replyRepository.findByIdAndMember_Id(replyId,
            userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPLY));

        replyRepository.delete(replyEntity);
    }

    @Transactional
    public void deleteReplies(List<Long> replyIds) {
        List<ReplyEntity> replyEntities = replyRepository.findAllById(replyIds);

        if (replyEntities.size() < replyIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SOME_Reply);
        }

        replyRepository.deleteAll(replyEntities);
    }

    private void validateSortField(Pageable pageable) {
        pageable.getSort().stream().forEach(order -> {
            if (!ALLOWED_SORT_FIELD.contains(order.getProperty())) {
                throw new CustomException(ErrorCode.INVALID_SORT);
            }
        });
    }
}
