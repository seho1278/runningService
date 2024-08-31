package com.example.runningservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.runningservice.config.QueryDslConfig;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.repository.crewMember.CrewMemberRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
@Import(QueryDslConfig.class)
public class CrewMemberRepositoryTest {

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Test
    void test_findByCrewIdOrderByRoleOrderAscJoinedAtAsc() {
        //given
        Long crewId = 1L;
        CrewEntity crew = CrewEntity.builder()
            .id(crewId)
            .build();

        CrewMemberEntity crewMember1 = CrewMemberEntity.builder()
            .crew(crew)
            .id(1L)
            .role(CrewRole.LEADER)
            .roleOrder(CrewRole.LEADER.getOrder())
            .joinedAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .build();
        System.out.println("crewMember1: " + crewMember1.getRoleOrder());
        CrewMemberEntity crewMember2 = CrewMemberEntity.builder()
            .crew(crew)
            .id(2L)
            .role(CrewRole.MEMBER)
            .roleOrder(CrewRole.MEMBER.getOrder())
            .joinedAt(LocalDateTime.of(2024, 1, 1, 1, 3))
            .build();
        System.out.println("crewMember2: " + crewMember2.getRoleOrder());
        CrewMemberEntity crewMember3 = CrewMemberEntity.builder()
            .crew(crew)
            .id(3L)
            .role(CrewRole.STAFF)
            .roleOrder(CrewRole.STAFF.getOrder())
            .joinedAt(LocalDateTime.of(2024, 1, 1, 1, 2))
            .build();
        System.out.println("crewMember3: " + crewMember3.getRoleOrder());
        crewRepository.save(crew);
        crewMemberRepository.save(crewMember1);
        crewMemberRepository.save(crewMember2);
        crewMemberRepository.save(crewMember3);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "joinedAt"));

        //when
        Page<CrewMemberEntity> result = crewMemberRepository.findByCrew_IdOrderByRoleOrderAsc(
            crewId, pageable);

        //then
        assertEquals(3, result.getTotalElements());
        assertEquals(CrewRole.LEADER, result.getContent().get(0).getRole());
        assertEquals(CrewRole.STAFF, result.getContent().get(1).getRole());
        assertEquals(CrewRole.MEMBER, result.getContent().get(2).getRole());
    }

}
