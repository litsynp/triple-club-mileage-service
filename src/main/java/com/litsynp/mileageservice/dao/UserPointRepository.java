package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.UserPoint;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPoint, UUID>,
        UserPointQueryRepository {

    List<UserPoint> findByReviewId(UUID reviewId);
}
