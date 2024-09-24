package com.example.runningservice.controller.post;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.service.post.PostService;
import com.example.runningservice.util.JwtUtil;
import com.example.runningservice.util.S3FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {

    @MockBean
    private PostService postService;

    @MockBean
    private S3FileUtil s3FileUtil;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시물 개별 조회 테스트")
    @WithMockUser("USER")
    void getPostTest() throws Exception {
        // Given
        Long crewId = 1L;
        Long userId = 3L;
        Long postId = 2L;

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .title("게시물1")
            .member(MemberEntity.builder().id(userId).nickName("nick1").build())
            .crewId(crewId)
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .isNotice(false)
            .images(List.of("image1"))
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .updatedAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .build();

        when(postService.getPost(postId)).thenReturn(postEntity);
        when(s3FileUtil.createPresignedUrl("image1")).thenReturn("signed_image1");
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/crew/{crewId}/post", crewId)
                .param("postId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.content").value("content"))  // 3 notice + 3 general posts
            .andExpect(jsonPath("$.title").value("게시물1"))
            .andExpect(jsonPath("$.images.size()").value(1))
            .andExpect(jsonPath("$.images[0]").value("signed_image1"));
    }

    @Test
    @DisplayName("게시물 목록 조회 테스트")
    @WithMockUser("USER")
    void getPostsTest() throws Exception {
        // Given
        Long crewId = 1L;

        PostEntity postEntity1 = PostEntity.builder().id(1L)
            .member(MemberEntity.builder().nickName("nick1").build()).title("공지사항1").build();
        PostEntity postEntity2 = PostEntity.builder().id(2L)
            .member(MemberEntity.builder().nickName("nick1").build()).title("공지사항2").build();
        PostEntity postEntity3 = PostEntity.builder().id(3L)
            .member(MemberEntity.builder().nickName("nick1").build()).title("공지사항3").build();

        // Mocking for notice posts
        List<PostEntity> noticePosts = List.of(postEntity1, postEntity2, postEntity3);
        Page<PostEntity> noticePostPage = new PageImpl<>(noticePosts, PageRequest.of(0, 5),
            noticePosts.size());

        PostEntity postEntity4 = PostEntity.builder().id(1L)
            .member(MemberEntity.builder().nickName("nick2").build()).title("게시물1").build();
        PostEntity postEntity5 = PostEntity.builder().id(2L)
            .member(MemberEntity.builder().nickName("nick2").build()).title("게시물2").build();
        PostEntity postEntity6 = PostEntity.builder().id(3L)
            .member(MemberEntity.builder().nickName("nick1").build()).title("게시물3").build();

        // Mocking for general posts
        List<PostEntity> generalPosts = List.of(postEntity4, postEntity5, postEntity6);
        Page<PostEntity> generalPostPage = new PageImpl<>(generalPosts,
            PageRequest.of(0, 10 - noticePosts.size()),
            generalPosts.size());

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        // Mock PostService responses
        when(postService.getNoticePost(crewId)).thenReturn(noticePostPage);
        when(postService.getPosts(eq(crewId), argThat(e -> e.getPageNumber() == 0 &&
                e.getPageSize() == 5 &&
                e.getSort().equals(Sort.by("createdAt").descending())),
            argThat(e -> e.getPostCategory().equals(PostCategory.PERSONAL)))).thenReturn(
            generalPostPage);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/crew/{crewId}/posts", crewId)
                .param("page", "0")
                .param("size", "10")
                .param("postCategory", "PERSONAL")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.content", hasSize(6)))  // 3 notice + 3 general posts
            .andExpect(jsonPath("$.content[0].postTitle").value("공지사항1"))
            .andExpect(jsonPath("$.content[1].postTitle").value("공지사항2"))
            .andExpect(jsonPath("$.content[2].postTitle").value("공지사항3"))
            .andExpect(jsonPath("$.content[3].postTitle").value("게시물1"))
            .andExpect(jsonPath("$.content[4].postTitle").value("게시물2"));
    }


    @Test
    @DisplayName("운영진 게시물 삭제 성공 테스트")
    @WithMockUser("ROLE_USER")
    void deletePosts_Success() throws Exception {
        // Given
        Long crewId = 1L;
        List<Long> postIds = List.of(1L, 2L, 3L);

        // When & Then
        mockMvc.perform(delete("/crew/{crewId}/posts", crewId)
                .param("postIds", "1", "2", "3")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // postService.deletePosts 호출 여부 확인
        verify(postService).deletePosts(postIds);
    }
}