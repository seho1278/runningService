package com.example.runningservice.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActivityIdValidator implements ConstraintValidator<RequiredActivityId, Object> {

    private String categoryFieldName;
    private String idFieldName;
    private Class<?> categoryFieldType;
    private Class<?> idFieldType;

    @Override
    public void initialize(RequiredActivityId constraintAnnotation) {
        // 어노테이션에서 필드명과 필드 타입 가져오기
        this.categoryFieldName = constraintAnnotation.category();
        this.idFieldName = constraintAnnotation.id();
        this.categoryFieldType = constraintAnnotation.categoryType();
        this.idFieldType = constraintAnnotation.idType();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        try {
            log.info("Dto class : {}", dto.getClass());

            // Reflection으로 필드를 가져옴
            Field postCategoryField = getFieldFromHierarchy(dto.getClass(), categoryFieldName);
            Field activityIdField = getFieldFromHierarchy(dto.getClass(), idFieldName);

            // 필드에 접근 가능하도록 설정
            postCategoryField.setAccessible(true);
            activityIdField.setAccessible(true);

            // 필드 값과 타입 가져오기
            Object postCategory = postCategoryField.get(dto);
            Object activityId = activityIdField.get(dto);

            log.info("PostCategory : {}", postCategory);
            log.info("ActivityId : {}", activityId);

            // 필드 타입이 어노테이션에서 지정한 타입과 일치하는지 확인
            if (!postCategoryField.getType().equals(categoryFieldType) || !activityIdField.getType().equals(idFieldType)) {
                throw new IllegalArgumentException("Field types do not match the expected types");
            }

            // PostCategory가 ACTIVITY_REVIEW일 때 activityId가 null이면 유효하지 않음
            if (postCategory != null && postCategory.toString().equals("ACTIVITY_REVIEW") && activityId == null) {
                return false;
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 필드를 찾지 못했거나 접근할 수 없는 경우는 검증 실패로 처리
            return false;
        }

        return true;
    }

    private Field getFieldFromHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();  // 상위 클래스에서 필드를 찾음
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy.");
    }
}