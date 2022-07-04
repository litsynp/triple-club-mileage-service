package com.litsynp.mileageservice.dao;

import static com.litsynp.mileageservice.domain.QUserPoint.userPoint;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointQueryRepositoryImpl implements UserPointQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Long getUserPoints(UUID userId) {
        return query
                .select(userPoint.amount.sum().coalesce(0L))
                .from(userPoint)
                .where(userPoint.user.id.eq(userId))
                .fetchOne();
    }
}
