package com.example.runningservice.util.validator;

import com.example.runningservice.repository.crew.CrewRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueCrewNameValidator implements ConstraintValidator<UniqueCrewName, String> {

    private final CrewRepository crewRepository;

    @Override
    public void initialize(UniqueCrewName constraintAnnotation) {

    }

    @Override
    public boolean isValid(String crewName, ConstraintValidatorContext context) {
        return !crewRepository.existsByCrewName(crewName);
    }
}
