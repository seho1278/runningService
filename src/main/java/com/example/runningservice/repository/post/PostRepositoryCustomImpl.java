package com.example.runningservice.repository.post;

import com.example.runningservice.dto.post.GetPostRequestDto.Filter;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.entity.post.QPostEntity;
import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.util.QueryDslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{

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

    private BooleanExpression postCategoryEq(PostCategory postCategory) {
        return postCategory != null ? QPostEntity.postEntity.postCategory.eq(postCategory) : null;
    }
}
