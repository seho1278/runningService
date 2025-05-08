package com.example.runningservice.util.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BooleanToIntegerConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {

        if (dbData == null) {
            return null;
        }
        return dbData == 1;
    }
}
