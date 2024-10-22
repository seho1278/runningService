package com.example.runningservice.dto.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.runningservice.enums.PostCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Validator 객체를 초기화
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("활동후기 게시판일 때 activityId가 null_실패")
    void testValidActivityId_WhenPostCategoryIsActivityReview_AndActivityIdIsNull_Failed() {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
            .title("Test Post")
            .content("This is a test post.")
            .postCategory(PostCategory.ACTIVITY_REVIEW) // 활동 리뷰일 때
            .activityId(null) // ActivityId가 null
            .build();

        // when
        Set<ConstraintViolation<PostRequestDto>> violations = validator.validate(requestDto);

        // then
        assertEquals(1, violations.size()); // 1개의 유효성 검증 실패
        assertTrue(violations.stream().anyMatch(
            v -> v.getMessage().contains("ACTIVITY_REVIEW 게시물 생성 시 Activity Id는 필수입니다.")
        ));
    }

    @Test
    void testValidActivityId_WhenPostCategoryIsActivityReview_AndActivityIdIsProvided_Success() {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
            .title("Test Post")
            .content("This is a test post.")
            .postCategory(PostCategory.ACTIVITY_REVIEW) // 활동 리뷰일 때
            .activityId(100L) // ActivityId가 제공됨
            .build();

        // when
        Set<ConstraintViolation<PostRequestDto>> violations = validator.validate(requestDto);

        // then
        assertTrue(violations.isEmpty()); // 유효성 검증 성공
    }

    @Test
    void testValidActivityId_WhenPostCategoryIsNotActivityReview_AndActivityIdIsNull_ShouldPass() {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
            .title("Test Post")
            .content("This is a test post.")
            .postCategory(PostCategory.PERSONAL) // 개인 게시물
            .activityId(null) // ActivityId가 없어도 됨
            .build();

        // when
        Set<ConstraintViolation<PostRequestDto>> violations = validator.validate(requestDto);

        // then
        assertTrue(violations.isEmpty()); // 유효성 검증 성공
    }
}