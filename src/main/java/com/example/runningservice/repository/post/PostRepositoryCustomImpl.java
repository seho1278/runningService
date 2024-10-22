package com.example.runningservice.repository.post;

import com.example.runningservice.dto.post.GetPostRequestDto.Filter;
import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.post.QGetPostSimpleResponseDto;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.entity.post.QPostEntity;
import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.enums.SearchType;
import com.example.runningservice.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<PostEntity> findAllNotNoticeByCrewIdAndFilter(Long crewId, Filter filter,
        Pageable pageable) {

        QPostEntity post = QPostEntity.postEntity;

        List<PostEntity> posts = queryFactory.selectFrom(post)
            .where(
                post.crewId.eq(crewId),
                postCategoryEq(filter.getPostCategory()),
                post.isNotice.eq(false)
            )
            .orderBy(QueryDslUtil.getAllOrderSpecifiers(pageable,
                "postEntity")) //page -> orderSpecifier
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        // 총 개수 계산

        Long total = queryFactory.select(post.count())
            .from(post)
            .where(
                post.crewId.eq(crewId),
                postCategoryEq(filter.getPostCategory()),
                post.isNotice.eq(false)
            )
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0);
    }

    @Override
    public Page<GetPostSimpleResponseDto> searchPostsByCrewIdAndKeyword(Long crewId,
        String keyword,
        SearchType searchType, Pageable pageable) {
        QPostEntity post = QPostEntity.postEntity;

        BooleanBuilder searchCondition = buildSearchCondition(crewId, keyword, searchType);

        List<GetPostSimpleResponseDto> results = queryFactory
            .select(new QGetPostSimpleResponseDto(post.id, post.title, post.member.nickName,
                post.createdAt, post.updatedAt))
            .from(post)
            .where(searchCondition)
            .orderBy(QueryDslUtil.getAllOrderSpecifiers(pageable, "postEntity"))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(post.count())
            .from(post)
            .where(searchCondition)
            .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression postCategoryEq(PostCategory postCategory) {
        return postCategory != null ? QPostEntity.postEntity.postCategory.eq(postCategory) : null;
    }

    private BooleanBuilder buildSearchCondition(Long crewId, String keyword,
        SearchType searchType) {
        QPostEntity post = QPostEntity.postEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.crewId.eq(crewId));
        if (searchType == SearchType.TITLE_CONTENT && keyword != null) {
            builder.and(titleContentContains(keyword));
        } else if (searchType == SearchType.AUTHOR && keyword != null) {
            builder.and(authorContainsIgnoreCase(keyword));
        }
        return builder;
    }

    private BooleanExpression titleContentContains(String keyword) {
        return keyword != null ? QPostEntity.postEntity.title.containsIgnoreCase(keyword)
            .or(QPostEntity.postEntity.content.containsIgnoreCase(keyword)) : null;
    }

    private BooleanExpression authorContainsIgnoreCase(String author) {
        return author != null ? QPostEntity.postEntity.member.nickName.contains(author)
            : null;
    }
}
