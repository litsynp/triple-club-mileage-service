package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.UserPoint;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPoint, UUID>,
        UserPointQueryRepository {

    Long deleteAllByReviewId(UUID reviewId);
}
