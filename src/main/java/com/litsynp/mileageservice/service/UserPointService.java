package com.litsynp.mileageservice.service;

import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.dao.UserRepository;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.domain.UserPoint;
import com.litsynp.mileageservice.global.error.exception.NotFoundFieldException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;

    public Long getUserPoints(UUID userId) {
        // 사용자 존재 확인
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundFieldException(
                    User.class.getSimpleName(),
                    "id",
                    userId.toString());
        }

        return userPointRepository.getAllUserPoints(userId);
    }

    public Page<UserPoint> search(Pageable pageable, UUID userId, UUID reviewId) {
        return userPointRepository.search(pageable, userId, reviewId);
    }
}
