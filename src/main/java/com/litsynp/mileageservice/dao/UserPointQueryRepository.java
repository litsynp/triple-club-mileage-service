package com.litsynp.mileageservice.dao;

import com.litsynp.mileageservice.domain.UserPoint;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserPointQueryRepository {

    Long getAllUserPoints(UUID userId);

    Long getUserPointsFromReview(UUID userId, UUID reviewId);

    Page<UserPoint> search(Pageable pageable, UUID userId, UUID reviewId);
}
