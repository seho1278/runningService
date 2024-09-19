package com.example.runningservice.service.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.post.PostRequestDto;
import com.example.runningservice.dto.post.UpdatePostRequestDto;
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

        PostRequestDto requestDto = PostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .imagesToUpload(List.of(mockFile1, mockFile2))
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
            requestDto.getImagesToUpload())).thenReturn(List.of("post-1-0", "post-1-1"));

        //when
        PostEntity result = postService.savePost(userId, crewId, requestDto);

        //then
        assertEquals(2, result.getImages().size());
        assertTrue(result.getImages().contains("post-1-0"));
        assertTrue(result.getImages().contains("post-1-1"));
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

        PostRequestDto requestDto = PostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .imagesToUpload(List.of(mockFile1, mockFile2))
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

        PostRequestDto requestDto = PostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .imagesToUpload(List.of(mockFile1, mockFile2))
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

        PostRequestDto requestDto = PostRequestDto.builder()
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .activityId(100L)
            .imagesToUpload(List.of(mockFile1, mockFile2))
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
            requestDto.getImagesToUpload())).thenReturn(List.of("post-1-0", "post-1-1"));

        //when
        PostEntity result = postService.savePost(userId, crewId, requestDto);

        //then
        assertEquals(2, result.getImages().size());
        assertTrue(result.getImages().contains("post-1-0"));
        assertTrue(result.getImages().contains("post-1-1"));
        assertEquals("title", result.getTitle());
        assertEquals("content", result.getContent());
        assertEquals(PostCategory.PERSONAL, result.getPostCategory());
        assertNull(result.getActivityId());
    }

    @Test
    @DisplayName("이미지 일부 삭제")
    void testUpdatePost_Success_DeleteSomeImages() {
        //given
        Long userId = 1L;
        Long crewId = 2L;
        Long postId = 3L;
        Long crewMemberId = 4L;

        UpdatePostRequestDto requestDto = UpdatePostRequestDto.builder()
            .postId(postId)
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .deleteAllImages(false)
            .imagesToDelete(List.of("test1"))
            .isNotice(false)
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .crewId(crewId)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .postCategory(requestDto.getPostCategory())
            .isNotice(requestDto.getIsNotice())
            .images(List.of("test1", "test2"))
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
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        //when
        PostEntity result = postService.updatePost(userId, crewId, requestDto);

        //then
        assertEquals(1, result.getImages().size());
        assertFalse(result.getImages().contains("test1"));
        assertTrue(result.getImages().contains("test2"));
    }

    @Test
    @DisplayName("이미지 전체 삭제")
    void testUpdatePost_Success_DeleteAllImages() {
        //given
        Long userId = 1L;
        Long crewId = 2L;
        Long postId = 3L;
        Long crewMemberId = 4L;

        UpdatePostRequestDto requestDto = UpdatePostRequestDto.builder()
            .postId(postId)
            .title("title")
            .content("content")
            .postCategory(PostCategory.PERSONAL)
            .deleteAllImages(true)
            .imagesToDelete(List.of("test1"))
            .isNotice(false)
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .crewId(crewId)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .postCategory(requestDto.getPostCategory())
            .isNotice(requestDto.getIsNotice())
            .images(List.of("test1", "test2"))
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
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        //when
        PostEntity result = postService.updatePost(userId, crewId, requestDto);

        //then
        assertEquals(0, result.getImages().size());
        assertFalse(result.getImages().contains("test1"));
        assertFalse(result.getImages().contains("test2"));
    }

    @Test
    @DisplayName("이미지 일부 삭제, 일부 추가")
    void testUpdatePost_Success_DeleteSomeImages_AddSomeImages() {
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

        UpdatePostRequestDto requestDto = UpdatePostRequestDto.builder()
            .postId(postId)
            .title("title_수정")
            .content("content_수정")
            .postCategory(PostCategory.PERSONAL)
            .deleteAllImages(false)
            .imagesToDelete(List.of("test1"))
            .imagesToUpload(List.of(mockFile1))
            .isNotice(false)
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .crewId(crewId)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .postCategory(requestDto.getPostCategory())
            .isNotice(requestDto.getIsNotice())
            .images(List.of("test1", "test2"))
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
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(s3FileUtil.uploadFilesAndReturnFileNames("post", postId,
            requestDto.getImagesToUpload())).thenReturn(List.of("test3"));
        //when
        PostEntity result = postService.updatePost(userId, crewId, requestDto);

        //then
        assertEquals(2, result.getImages().size());
        assertTrue(result.getImages().contains("test2"));
        assertTrue(result.getImages().contains("test3"));
        assertFalse(result.getImages().contains("test1"));
        assertEquals("title_수정", result.getTitle());
    }

    @Test
    @DisplayName("이미지는 아무 변경 하지 않음")
    void testUpdatePost_Success_NoChangeWithImages() {
        //given
        Long userId = 1L;
        Long crewId = 2L;
        Long postId = 3L;
        Long crewMemberId = 4L;

        UpdatePostRequestDto requestDto = UpdatePostRequestDto.builder()
            .postId(postId)
            .title("title_수정")
            .content("content_수정")
            .postCategory(PostCategory.PERSONAL)
            .deleteAllImages(false)
            .isNotice(false)
            .build();

        PostEntity postEntity = PostEntity.builder()
            .id(postId)
            .crewId(crewId)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .postCategory(requestDto.getPostCategory())
            .isNotice(requestDto.getIsNotice())
            .images(List.of("test1", "test2"))
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
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        //when
        PostEntity result = postService.updatePost(userId, crewId, requestDto);

        //then
        assertEquals(2, result.getImages().size());
        assertTrue(result.getImages().contains("test2"));
        assertTrue(result.getImages().contains("test1"));
        assertEquals("title_수정", result.getTitle());
    }
}