package com.litsynp.mileageservice.dao;

import java.util.UUID;

public interface UserPointQueryRepository {

    Long getAllUserPoints(UUID userId);

    Long getUserPointsFromReview(UUID userId, UUID reviewId);
}
