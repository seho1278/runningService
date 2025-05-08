package com.example.runningservice.service.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ReplyService replyService;

    @Test
    @DisplayName("댓글 생성 성공")
    void testWriteReply_Success() {
        //given
        Long postId = 1L;
        Long userId = 2L;

        CreateReplyRequestDto requestDto = CreateReplyRequestDto.builder()
            .postId(postId)
            .content("content")
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .build();

        MemberEntity memberEntity = MemberEntity.builder().id(userId).build();

        ReplyEntity replyEntity = ReplyEntity.of(postEntity, memberEntity, requestDto.getContent());

        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(memberRepository.findById(userId)).thenReturn(Optional.of(memberEntity));
        when(replyRepository.save(argThat(e -> e.getPost().equals(replyEntity.getPost()) &&
            e.getMember().equals(replyEntity.getMember()) &&
            e.getContent().equals(replyEntity.getContent())))).thenReturn(replyEntity);

        //when
        ReplyEntity result = replyService.writeReply(userId, requestDto);

        //then
        assertEquals(userId, result.getMember().getId());
        assertEquals(postId, result.getPost().getId());
        assertEquals("content", result.getContent());
    }

    @Test
    @DisplayName("")
    void testUpdateReply_Success() {
        //given
        Long userId = 1L;
        Long replyId = 2L;

        UpdateReplyRequestDto requestDto = UpdateReplyRequestDto.builder()
            .replyId(replyId)
            .content("content_update")
            .build();

        ReplyEntity replyEntity = ReplyEntity.builder()
            .id(replyId)
            .content("content")
            .member(MemberEntity.builder().id(userId).build())
            .build();

        when(replyRepository.findByIdAndMember_Id(replyId, userId)).thenReturn(
            Optional.of(replyEntity));

        //when
        ReplyEntity result = replyService.updateReply(userId, requestDto);

        //then
        assertEquals("content_update", result.getContent());
        assertEquals(userId, result.getMember().getId());
    }

    @Test
    @DisplayName("특정 게시물의 댓글 조회")
    void testGetReplies_Success() {
        //given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        ReplyEntity reply1 = ReplyEntity.builder().id(1L).content("content1").build();
        ReplyEntity reply2 = ReplyEntity.builder().id(2L).content("content2").build();
        ReplyEntity reply3 = ReplyEntity.builder().id(3L).content("content3").build();
        ReplyEntity reply4 = ReplyEntity.builder().id(4L).content("content4").build();
        ReplyEntity reply5 = ReplyEntity.builder().id(5L).content("content5").build();
        ReplyEntity reply6 = ReplyEntity.builder().id(6L).content("content6").build();
        ReplyEntity reply7 = ReplyEntity.builder().id(7L).content("content7").build();
        ReplyEntity reply8 = ReplyEntity.builder().id(8L).content("content8").build();
        ReplyEntity reply9 = ReplyEntity.builder().id(9L).content("content9").build();
        ReplyEntity reply10 = ReplyEntity.builder().id(10L).content("content10").build();

        Page<ReplyEntity> pageReply = new PageImpl<>(
            List.of(reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10,
                reply10));

        when(replyRepository.findAllByPost_Id(postId, pageable)).thenReturn(pageReply);

        //when
        Page<ReplyEntity> result = replyService.getReplies(postId, pageable);

        //then
        assertEquals(11, result.getTotalElements());
        assertEquals("content1", result.getContent().get(0).getContent());
        assertEquals("content2", result.getContent().get(1).getContent());
        assertEquals("content3", result.getContent().get(2).getContent());
    }

    @Test
    @DisplayName("나의 댓글 조회")
    void testGetMyReplies_Success() {
        //given
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        ReplyEntity reply1 = ReplyEntity.builder().id(1L).content("content1").build();
        ReplyEntity reply2 = ReplyEntity.builder().id(2L).content("content2").build();
        ReplyEntity reply3 = ReplyEntity.builder().id(3L).content("content3").build();

        Page<ReplyEntity> pageReply = new PageImpl<>(List.of(reply1, reply2, reply3));

        when(replyRepository.findAllByMember_Id(userId, pageable)).thenReturn(pageReply);

        //when
        Page<ReplyEntity> myReplies = replyService.getMyReplies(userId, pageable);

        //then
        assertEquals(3, myReplies.getTotalElements());
        assertEquals(1L, myReplies.getContent().get(0).getId());
        assertEquals(2L, myReplies.getContent().get(1).getId());
        assertEquals(3L, myReplies.getContent().get(2).getId());
    }

    @Test
    @DisplayName("나의 댓글 조회_허용되지 않는 정렬기준_실패")
    void testGetMyReplies_Failed_NotAllowedSortProperty() {
        //given
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("updatedAt").descending());

        ReplyEntity reply1 = ReplyEntity.builder().id(1L).content("content1").build();
        ReplyEntity reply2 = ReplyEntity.builder().id(2L).content("content2").build();
        ReplyEntity reply3 = ReplyEntity.builder().id(3L).content("content3").build();

        Page<ReplyEntity> pageReply = new PageImpl<>(List.of(reply1, reply2, reply3));

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> replyService.getMyReplies(userId, pageable));

        //then
        assertEquals(ErrorCode.INVALID_SORT, exception.getErrorCode());
    }

    @Test
    void testDeleteReply_Success() {
        //given
        Long userId = 1L;
        Long replyId = 2L;

        ReplyEntity replyEntity = ReplyEntity.builder()
            .id(replyId)
            .member(MemberEntity.builder().id(userId).build())
            .content("content")
            .build();

        when(replyRepository.findByIdAndMember_Id(replyId, userId)).thenReturn(Optional.of(replyEntity));
        //when
        replyService.deleteReply(userId, replyId);

        //then
        verify(replyRepository, times(1)).delete(replyEntity);
    }

    @Test
    void testDeleteReplies_Success() {
        //given
        List<Long> replyIds = List.of(1L, 2L, 3L);

        ReplyEntity replyEntity1 = ReplyEntity.builder().id(1L).content("content1").build();
        ReplyEntity replyEntity2 = ReplyEntity.builder().id(2L).content("content2").build();
        ReplyEntity replyEntity3 = ReplyEntity.builder().id(3L).content("content3").build();

        List<ReplyEntity> replyEntities = List.of(replyEntity1, replyEntity2, replyEntity3);

        when(replyRepository.findAllById(replyIds)).thenReturn(replyEntities);

        //when
        replyService.deleteReplies(replyIds);

        //then
        verify(replyRepository, times(1)).deleteAll(replyEntities);
    }
}