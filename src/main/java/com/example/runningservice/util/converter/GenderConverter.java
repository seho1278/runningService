package com.example.runningservice.util.converter;

import com.example.runningservice.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Gender gender) {
        return gender != null ? gender.getCode() : null;
    }

    @Override
    public Gender convertToEntityAttribute(Integer dbData) {
        return dbData != null ? Gender.fromCode(dbData) : null;
    }
}