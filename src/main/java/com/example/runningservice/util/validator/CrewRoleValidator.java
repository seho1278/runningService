package com.example.runningservice.util.validator;

import com.example.runningservice.enums.CrewRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class CrewRoleValidator implements ConstraintValidator<CrewRoleChangeValid,CrewRole> {
    private List<String> acceptedRoles;

    @Override
    public void initialize(CrewRoleChangeValid annotation) {
        acceptedRoles = Arrays.asList(annotation.roles());
    }

    @Override
    public boolean isValid(CrewRole role, ConstraintValidatorContext context) {
        return role != null && acceptedRoles.contains(role.name());
    }
}
