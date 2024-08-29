package com.example.runningservice.repository.crewMember;

import com.example.runningservice.dto.crewMember.GetCrewMemberRequestDto;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.QCrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.util.QueryDslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CrewMemberRepositoryCustomImpl implements CrewMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CrewMemberEntity> findAllByCrewIdAndFilter(Long crewId,
        GetCrewMemberRequestDto.Filter filterDto,
        Pageable pageable) {

        QCrewMemberEntity crewMember = QCrewMemberEntity.crewMemberEntity;
        // 쿼리 작성

        List<CrewMemberEntity> crewMembers = queryFactory.selectFrom(crewMember)
            .where(
                crewIdEq(crewId),
                genderEq(filterDto.getGender()),
                roleEq(filterDto.getCrewRole()),
                birthYearGoe(filterDto.getMaxYear()),
                birthYearLoe(filterDto.getMinYear())
            )
            .orderBy(QueryDslUtil.getAllOrderSpecifiers(pageable,
                "crewMemberEntity")) //page -> orderSpecifier
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        // 총 개수 계산

        Long total = queryFactory.select(crewMember.count())
            .from(crewMember)
            .where(
                crewIdEq(crewId),
                genderEq(filterDto.getGender()),
                roleEq(filterDto.getCrewRole()),
                birthYearGoe(filterDto.getMaxYear()),
                birthYearLoe(filterDto.getMinYear())
            )
            .fetchOne();

        return new PageImpl<>(crewMembers, pageable, total != null ? total : 0);
    }

    private BooleanExpression crewIdEq(Long crewId) {
        return crewId != null ? QCrewMemberEntity.crewMemberEntity.crew.id.eq(crewId) : null;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender != null ? QCrewMemberEntity.crewMemberEntity.member.gender.eq(gender) : null;
    }

    private BooleanExpression roleEq(CrewRole crewRole) {
        return crewRole != null ? QCrewMemberEntity.crewMemberEntity.role.eq(crewRole) : null;
    }

    private BooleanExpression birthYearGoe(Integer maxYear) {
        if (maxYear == null) {
            return null;
        }
        return QCrewMemberEntity.crewMemberEntity.member.birthYear.goe(maxYear);
    }

    private BooleanExpression birthYearLoe(Integer minYear) {
        if (minYear == null) {
            return null;
        }
        return QCrewMemberEntity.crewMemberEntity.member.birthYear.loe(minYear);
    }
}
