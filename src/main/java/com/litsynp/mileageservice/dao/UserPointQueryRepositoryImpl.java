package com.litsynp.mileageservice.dao;

import static com.litsynp.mileageservice.domain.QReview.review;
import static com.litsynp.mileageservice.domain.QUser.user;
import static com.litsynp.mileageservice.domain.QUserPoint.userPoint;

import com.litsynp.mileageservice.domain.QUser;
import com.litsynp.mileageservice.domain.UserPoint;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointQueryRepositoryImpl implements UserPointQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Long getAllUserPoints(UUID userId) {
        return query
                .select(userPoint.amount.sum().coalesce(0L))
                .from(userPoint)
                .where(userPoint.user.id.eq(userId))
                .fetchOne();
    }

    @Override
    public Long getUserPointsFromReview(UUID userId, UUID reviewId) {
        return query
                .select(userPoint.amount.sum().coalesce(0L))
                .from(userPoint)
                .innerJoin(userPoint.review, review)
                .where(userPoint.user.id.eq(userId), reviewIdEq(reviewId))
                .fetchOne();
    }

    @Override
    public Page<UserPoint> search(Pageable pageable, UUID userId, UUID reviewId) {
        JPAQuery<UserPoint> contentQuery = query
                .selectFrom(userPoint)
                .leftJoin(userPoint.user, user).fetchJoin()
                .leftJoin(userPoint.review, review).fetchJoin()
                .where(userIdEq(userId), reviewIdEq(reviewId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(userPoint.getType(), userPoint.getMetadata());
            contentQuery.orderBy(new OrderSpecifier<>(o.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(o.getProperty())));
        }
        List<UserPoint> content = contentQuery.fetch();

        JPAQuery<Long> countQuery = getCount(userId, reviewId);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private JPAQuery<Long> getCount(UUID userId, UUID reviewId) {
        return query
                .select(userPoint.count())
                .from(userPoint)
                .leftJoin(userPoint.user, user)
                .leftJoin(userPoint.review, review)
                .where(userIdEq(userId),
                        reviewIdEq(reviewId));
    }

    private BooleanExpression userIdEq(UUID userId) {
        return userId != null ? userPoint.user.id.eq(userId) : null;
    }

    private BooleanExpression reviewIdEq(UUID reviewId) {
        return reviewId != null ? userPoint.review.id.eq(reviewId) : null;
    }
}
