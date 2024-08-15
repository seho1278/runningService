package com.example.runningservice.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class YearRangeValidator implements ConstraintValidator<YearRange, Object> {

    private String minYear;
    private String maxYear;

    @Override
    public void initialize(YearRange constraintAnnotation) {
        this.minYear = constraintAnnotation.minYear();
        this.maxYear = constraintAnnotation.maxYear();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Integer minYear = getFieldValue(object, this.minYear);
        if (minYear == null) {
            return true;
        }
        Integer maxYear = getFieldValue(object, this.maxYear);
        if (maxYear == null) {
            return true;
        }
        return minYear >= maxYear;
    }

    private Integer getFieldValue(Object object, String fieldName) {
        Class<?> clazz = object.getClass().getSuperclass();
        Field yearField;
        try {
            yearField = clazz.getDeclaredField(fieldName);
            yearField.setAccessible(true);
            Object target = yearField.get(object);

            return (Integer) target;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
