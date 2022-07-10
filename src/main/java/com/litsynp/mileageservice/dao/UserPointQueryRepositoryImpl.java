package com.litsynp.mileageservice.dao;

import static com.litsynp.mileageservice.domain.QReview.review;
import static com.litsynp.mileageservice.domain.QUserPoint.userPoint;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
                .where(userPoint.user.id.eq(userId)
                        .and(reviewIdEq(reviewId)))
                .fetchOne();
    }

    private BooleanExpression reviewIdEq(UUID reviewId) {
        return reviewId != null ? userPoint.review.id.eq(reviewId) : null;
    }
}
