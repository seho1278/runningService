package com.example.runningservice.repository.post;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.runningservice.config.QueryDslConfig;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.entity.post.ReplyEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(QueryDslConfig.class)
@EntityScan(basePackages = {"com.example.runningservice.entity"})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class ReplyRepositoryTest {
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("댓글 페이지네이션 기능 테스트")
    void testPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

        PostEntity post = PostEntity.builder()
            .crewId(3L)
            .build();
        MemberEntity member1 = MemberEntity.builder()
            .email("email1")
            .build();
        MemberEntity member2 = MemberEntity.builder()
            .email("email2")
            .build();
        MemberEntity member3 = MemberEntity.builder()
            .email("email3")
            .build();
        MemberEntity member4 = MemberEntity.builder()
            .email("email4")
            .build();

        ReplyEntity reply1 = ReplyEntity.builder().post(post).member(member1).content("content1").build();
        ReplyEntity reply2 = ReplyEntity.builder().post(post).member(member2).content("content2").build();
        ReplyEntity reply3 = ReplyEntity.builder().post(post).member(member3).content("content3").build();
        ReplyEntity reply4 = ReplyEntity.builder().post(post).member(member4).content("content4").build();
        ReplyEntity reply5 = ReplyEntity.builder().post(post).member(member1).content("content5").build();
        ReplyEntity reply6 = ReplyEntity.builder().post(post).member(member2).content("content6").build();
        ReplyEntity reply7 = ReplyEntity.builder().post(post).member(member3).content("content7").build();
        ReplyEntity reply8 = ReplyEntity.builder().post(post).member(member4).content("content8").build();
        ReplyEntity reply9 = ReplyEntity.builder().post(post).member(member1).content("content9").build();
        ReplyEntity reply10 = ReplyEntity.builder().post(post).member(member2).content("content10").build();

        memberRepository.saveAll(List.of(member1, member2, member3, member4));
        postRepository.save(post);
        replyRepository.saveAll(List.of(reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8, reply9, reply10));

        // When
        Page<ReplyEntity> resultPage = replyRepository.findAllByPost_Id(post.getId(), pageable);

        // Then
        assertNotNull(resultPage);
        assertEquals(5, resultPage.getContent().size());
        assertEquals(2, resultPage.getTotalPages());
        assertEquals(0, resultPage.getNumber());
        assertEquals(10, resultPage.getTotalElements());
        assertTrue(resultPage.isFirst());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void testDeleteReply_Success() {
        //given

        MemberEntity member = MemberEntity.builder()
            .email("email1")
            .build();

        PostEntity post = PostEntity.builder()
            .crewId(3L)
            .build();

        ReplyEntity replyEntity = ReplyEntity.builder()
            .member(member)
            .post(post)
            .content("content")
            .build();

        memberRepository.save(member);
        postRepository.save(post);
        replyRepository.save(replyEntity);
        //when
        deleteReply(member.getId(), replyEntity.getId());

        //then
        Optional<ReplyEntity> deletedReply = replyRepository.findById(replyEntity.getId());
        assertTrue(deletedReply.isEmpty());
    }

    // 실제 삭제 메서드
    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        ReplyEntity replyEntity = replyRepository.findByIdAndMember_Id(replyId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPLY));

        replyRepository.delete(replyEntity);
    }
}
