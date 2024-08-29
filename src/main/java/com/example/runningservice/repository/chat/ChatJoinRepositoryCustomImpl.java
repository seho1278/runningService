package com.example.runningservice.repository.chat;

import static com.example.runningservice.entity.chat.QChatJoinEntity.chatJoinEntity;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatJoinRepositoryCustomImpl implements ChatJoinRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByMemberIdAndCrewId(Long memberId, Long crewId) {
        new JPADeleteClause(entityManager, chatJoinEntity)
            .where(chatJoinEntity.member.id.eq(memberId)
                .and(chatJoinEntity.chatRoom.crew.id.eq(crewId)))
            .execute();
    }
}
