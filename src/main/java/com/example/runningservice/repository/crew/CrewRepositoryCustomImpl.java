package com.example.runningservice.repository.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.QCrewEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewRepositoryCustomImpl implements CrewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CrewEntity> findByIdAndMemberCountLessThanCapacity(Long crewId) {
        QCrewEntity crew = QCrewEntity.crewEntity;

        CrewEntity foundCrew = queryFactory.selectFrom(crew)
            .where(crew.id.eq(crewId)
                .and(crew.crewMember.size().lt(crew.crewCapacity)))
            .fetchOne();

        return Optional.ofNullable(foundCrew);
    }
}
