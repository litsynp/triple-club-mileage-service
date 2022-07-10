package com.litsynp.mileageservice.service;

import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.dao.UserRepository;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.global.error.exception.NotFoundFieldException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;

    public Long getUserPoints(UUID userId) {
        // Check if user with userId exists
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundFieldException(
                    User.class.getSimpleName(),
                    "id",
                    userId.toString());
        }

        return userPointRepository.getAllUserPoints(userId);
    }
}
