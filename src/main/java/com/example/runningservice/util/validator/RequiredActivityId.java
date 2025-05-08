package com.example.runningservice.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ActivityIdValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredActivityId {
    String message() default "ACTIVITY_REVIEW 게시물 생성 시 Activity Id는 필수입니다.";

    String category(); // PostCategory 필드명
    String id(); // ActivityId 필드명

    // 필드 타입 속성
    Class<?> categoryType(); // PostCategory 필드 타입
    Class<?> idType(); // ActivityId 필드 타입

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
