package com.example.runningservice.service.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.post.CreatePostRequestDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import com.example.runningservice.repository.post.PostRepository;
import com.example.runningservice.util.S3FileUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    CrewMemberRepository crewMemberRepository;

    @Mock
    S3FileUtil s3FileUtil;

    @InjectMocks
    PostService postService;

    @Test
    void testCreatePost_Success() {
        //given
        Long userId = 1L;
        Long crewId = 2L;
        Long postId = 3L;
        Long crewMemberId = 4L;

        MockMultipartFile mockFile1 = new MockMultipartFile(
            "file",                         // 필드 이름
            "testImage1.jpg",                // 파일 이름
            "image/jpeg",                    // 파일 타입
            "Test image content 1".getBytes() // 파일 내용
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
            "file",
            "testImage2.jpg",
            "image/jpeg",
            "Test image content 2".getBytes()
        );

        CreatePostRequestDto requestDto = CreatePostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .images(List.of(mockFile1, mockFile2))
            .isNotice(false)
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .crewId(crewId)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .postCategory(requestDto.getPostCategory())
            .isNotice(requestDto.getIsNotice())
            .build();

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .id(crewMemberId)
            .crew(CrewEntity.builder().id(crewId).build())
            .member(MemberEntity.builder().id(userId).build())
            .role(CrewRole.MEMBER)
            .roleOrder(CrewRole.MEMBER.getOrder())
            .build();

        when(crewMemberRepository.findByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            Optional.of(crewMember));
        when(postRepository.save(argThat(i -> i.getTitle().equals(postEntity.getTitle()) &&
            i.getContent().equals(postEntity.getContent()) &&
            i.getPostCategory().equals(postEntity.getPostCategory()) &&
            i.getIsNotice().equals(postEntity.getIsNotice())))).thenReturn(postEntity);

        when(s3FileUtil.uploadFilesAndReturnFileNames("post", postId,
            requestDto.getImages())).thenReturn(List.of("post-1-0", "post-1-1"));

        //when
        PostEntity result = postService.savePost(userId, crewId, requestDto);

        //then
        assertEquals(2, result.getImageUrls().size());
        assertEquals("post-1-0", result.getImageUrls().get(0));
        assertEquals("post-1-1", result.getImageUrls().get(1));
        assertEquals("title", result.getTitle());
        assertEquals("content", result.getContent());
        assertEquals(PostCategory.PERSONAL, result.getPostCategory());
        assertEquals(false, result.getIsNotice());
    }

    @Test
    @DisplayName("크루원 아님_실패")
    void testCreatePost_Failed_NotCrewMember() {
        //given
        Long userId = 1L;
        Long crewId = 2L;

        MockMultipartFile mockFile1 = new MockMultipartFile(
            "file",                         // 필드 이름
            "testImage1.jpg",                // 파일 이름
            "image/jpeg",                    // 파일 타입
            "Test image content 1".getBytes() // 파일 내용
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
            "file",
            "testImage2.jpg",
            "image/jpeg",
            "Test image content 2".getBytes()
        );

        CreatePostRequestDto requestDto = CreatePostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .images(List.of(mockFile1, mockFile2))
            .isNotice(false)
            .build();

        when(crewMemberRepository.findByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> postService.savePost(userId, crewId, requestDto));

        //then
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 멤버가 공지사항 등록 시도_실패")
    void testSavePost_Failed_Unauthorized_Notice() {
        //given
        Long userId = 1L;
        Long crewId = 2L;
        Long postId = 3L;
        Long crewMemberId = 4L;

        MockMultipartFile mockFile1 = new MockMultipartFile(
            "file",                         // 필드 이름
            "testImage1.jpg",                // 파일 이름
            "image/jpeg",                    // 파일 타입
            "Test image content 1".getBytes() // 파일 내용
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
            "file",
            "testImage2.jpg",
            "image/jpeg",
            "Test image content 2".getBytes()
        );

        CreatePostRequestDto requestDto = CreatePostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .images(List.of(mockFile1, mockFile2))
            .isNotice(true)
            .build();


        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .id(crewMemberId)
            .crew(CrewEntity.builder().id(crewId).build())
            .member(MemberEntity.builder().id(userId).build())
            .role(CrewRole.MEMBER)
            .roleOrder(CrewRole.MEMBER.getOrder())
            .build();

        when(crewMemberRepository.findByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            Optional.of(crewMember));

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> postService.savePost(userId, crewId, requestDto));

        //then
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void testSavePost_Success_PERSONAL_ActivityIdNull() {
        //given
        Long userId = 1L;
        Long crewId = 2L;
        Long postId = 3L;
        Long crewMemberId = 4L;

        MockMultipartFile mockFile1 = new MockMultipartFile(
            "file",                         // 필드 이름
            "testImage1.jpg",                // 파일 이름
            "image/jpeg",                    // 파일 타입
            "Test image content 1".getBytes() // 파일 내용
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
            "file",
            "testImage2.jpg",
            "image/jpeg",
            "Test image content 2".getBytes()
        );

        CreatePostRequestDto requestDto = CreatePostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .activityId(100L)
            .images(List.of(mockFile1, mockFile2))
            .isNotice(false)
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .crewId(crewId)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .postCategory(requestDto.getPostCategory())
            .isNotice(requestDto.getIsNotice())
            .build();

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .id(crewMemberId)
            .crew(CrewEntity.builder().id(crewId).build())
            .member(MemberEntity.builder().id(userId).build())
            .role(CrewRole.MEMBER)
            .roleOrder(CrewRole.MEMBER.getOrder())
            .build();

        when(crewMemberRepository.findByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            Optional.of(crewMember));
        when(postRepository.save(argThat(i -> i.getTitle().equals(postEntity.getTitle()) &&
            i.getContent().equals(postEntity.getContent()) &&
            i.getPostCategory().equals(postEntity.getPostCategory()) &&
            i.getIsNotice().equals(postEntity.getIsNotice())))).thenReturn(postEntity);

        when(s3FileUtil.uploadFilesAndReturnFileNames("post", postId,
            requestDto.getImages())).thenReturn(List.of("post-1-0", "post-1-1"));

        //when
        PostEntity result = postService.savePost(userId, crewId, requestDto);

        //then
        assertEquals(2, result.getImageUrls().size());
        assertEquals("post-1-0", result.getImageUrls().get(0));
        assertEquals("post-1-1", result.getImageUrls().get(1));
        assertEquals("title", result.getTitle());
        assertEquals("content", result.getContent());
        assertEquals(PostCategory.PERSONAL, result.getPostCategory());
        assertNull(result.getActivityId());
    }
}