package com.litsynp.mileageservice.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.litsynp.mileageservice.config.QuerydslConfig;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.domain.UserPoint;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(QuerydslConfig.class)
@DisplayName("사용자 포인트 Repository")
class UserPointRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("Get user points :: Returns sum of user points :: OK")
    void getUserPoints() {
        // given
        User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
        userRepository.save(user);

        Place place = new Place(UUID.randomUUID(), "해운대 수변공원");
        placeRepository.save(place);

        Review review = new Review(UUID.randomUUID(), user, place, "또 방문하고 싶어요!");
        reviewRepository.save(review);

        List<UserPoint> userPoints = List.of(
                new UserPoint(UUID.randomUUID(), user, review, 10L),
                new UserPoint(UUID.randomUUID(), user, review, -15L),
                new UserPoint(UUID.randomUUID(), user, review, 30L)
        );
        userPointRepository.saveAll(userPoints);

        // when
        Long points = userPointRepository.getUserPoints(user.getId());

        // then
        assertThat(points).isEqualTo(25L);
    }
}
