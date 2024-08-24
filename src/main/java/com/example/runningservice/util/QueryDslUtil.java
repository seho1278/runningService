package com.example.runningservice.util;

import com.example.runningservice.entity.QCrewMemberEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class QueryDslUtil {
    //map으로 정렬할 entity 관리
    private static final Map<String, PathBuilder<?>> ENTITY_PATH_MAP = new HashMap<>();

    static {
        ENTITY_PATH_MAP.put("crewMemberEntity", new PathBuilder<>(QCrewMemberEntity.class, "crewMemberEntity"));
        // 새로운 엔티티가 추가될 때마다 여기에 추가
    }

    public static List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable, String entityType) {

        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            PathBuilder<?> entityPath = ENTITY_PATH_MAP.get(entityType);

            if (entityPath == null) {
                throw new IllegalArgumentException("Entity의 타입이 아닙니다.");
            }

            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                Expression fieldPath = Expressions.path(Object.class, entityPath, order.getProperty());
                OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(direction, fieldPath);
                orders.add(orderSpecifier);
            }
        }

        return orders;
    }

    private static boolean isEmpty(Sort sort) {
        return sort == null || !sort.iterator().hasNext();
    }
}
