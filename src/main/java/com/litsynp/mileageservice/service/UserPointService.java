package com.litsynp.mileageservice.service;

import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.domain.UserPoint;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;

    public Long getUserPoints(UUID userId) {
        return userPointRepository.getAllUserPoints(userId);
    }

    public Page<UserPoint> search(Pageable pageable, UUID userId, UUID reviewId) {
        return userPointRepository.search(pageable, userId, reviewId);
    }
}
