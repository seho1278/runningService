package com.example.runningservice.aop;

import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CrewRoleCheckAspect {

    private final CrewMemberRepository crewMemberRepository;

    @Before("execution(* com.example.runningservice.controller..*(..)) && @annotation(crewRoleCheck)")
    public void crewRoleCheckBeforeAccess(JoinPoint joinPoint, CrewRoleCheck crewRoleCheck) {
        Object[] args = joinPoint.getArgs();

        Long loginId = (Long) args[0];
        Long crewId = (Long) args[1];

        CrewMemberEntity crewMemberEntity = crewMemberRepository.findByCrew_CrewIdAndMember_Id(
                crewId, loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_CREW_ACCESS));

        if (crewRoleCheck.role().equals("leaderAndStaff")) {
            if (crewMemberEntity.getRole().equals(CrewRole.MEMBER)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_CREW_ACCESS);
            }
        }
    }
}
