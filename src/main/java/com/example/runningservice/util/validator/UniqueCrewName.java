package com.example.runningservice.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueCrewNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCrewName {

    String message() default "이미 존재하는 크루명입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
