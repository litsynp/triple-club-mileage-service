package com.litsynp.mileageservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.litsynp.mileageservice.dao.PlaceRepository;
import com.litsynp.mileageservice.dao.ReviewRepository;
import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.UserPoint;
import com.litsynp.mileageservice.global.config.QuerydslConfig;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({QuerydslConfig.class, UserPointService.class})
@DisplayName("사용자 포인트 Service")
public class UserPointServiceTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Nested
    @DisplayName("사용자 포인트 총점 조회")
    class GetUserPointsTest {

        @Test
        @DisplayName("OK")
        void getUserPoints_ok() {
            // given
            UUID userId = UUID.randomUUID();

            Place place = new Place(UUID.randomUUID(), "해운대 수변공원");
            placeRepository.save(place);

            Review review = new Review(UUID.randomUUID(), userId, place, "또 방문하고 싶어요!");
            reviewRepository.save(review);

            // 포인트 저장
            userPointRepository.save(new UserPoint(UUID.randomUUID(), userId, review, 2L));

            // when
            Long userPoints = userPointService.getUserPoints(userId);

            // then
            assertThat(userPoints).isEqualTo(2L);
        }
    }
}
