package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.runningservice.config.QueryDslConfig;
import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.repository.post.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
@Import(QueryDslConfig.class)
class SearchRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("keyword만 입력_searchtype은 기본값")
    void searchPostsByCrewIdAndKeywordAndAuthor() {
        //given
        Long crewId = 1L;
        String keyword = "tle";

        MemberEntity member1 = MemberEntity.builder()
            .email("email1")
            .build();
        MemberEntity member2 = MemberEntity.builder()
            .email("email2")
            .build();
        MemberEntity member3 = MemberEntity.builder()
            .email("email3")
            .build();

        PostEntity post1 = PostEntity.builder()
            .title("title1")
            .content("content1")
            .member(member1)
            .crewId(crewId)
            .createdAt(LocalDateTime.of(2024,1,1,1,1))
            .build();

        PostEntity post2 = PostEntity.builder()
            .title("title2")
            .content("content2")
            .member(member2)
            .crewId(crewId)
            .createdAt(LocalDateTime.of(2024,1,1,1,2))
            .build();

        PostEntity post3 = PostEntity.builder()
            .title("title3")
            .content("content3")
            .member(member3)
            .crewId(crewId)
            .createdAt(LocalDateTime.of(2024,1,1,1,3))
            .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt"));

        memberRepository.saveAll(List.of(member1, member2, member3));
        postRepository.saveAll(List.of(post1, post2, post3));
        System.out.println(postRepository.findAll().size() + "개 저장");
        //when
        Page<GetPostSimpleResponseDto> result = postRepository.searchPostsByCrewIdAndKeywordAndAuthor(
            crewId, keyword, null, pageable);

        System.out.println("result size: " + result.getTotalElements());

        //then
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSize());
        assertEquals(3, result.getContent().size());
        assertEquals("title3", result.getContent().get(0).getPostTitle());
    }
}