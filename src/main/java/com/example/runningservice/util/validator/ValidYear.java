package com.example.runningservice.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = YearValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYear {

    String message() default "1900 ~ 현재 연도 이내 값을 입력하세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
