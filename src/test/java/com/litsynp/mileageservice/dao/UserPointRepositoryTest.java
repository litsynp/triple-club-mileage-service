package com.litsynp.mileageservice.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.litsynp.mileageservice.config.QuerydslConfig;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.domain.UserPoint;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfig.class)
class UserPointRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Test
    @DisplayName("Get user points :: Returns sum of user points :: OK")
    void getUserPoints() {
        // given
        User user = new User("test@example.com", "12345678");
        userRepository.save(user);

        List<UserPoint> userPoints = List.of(
                new UserPoint(user, 10L),
                new UserPoint(user, -15L),
                new UserPoint(user, 30L)
        );
        userPointRepository.saveAll(userPoints);

        // when
        Long points = userPointRepository.getUserPoints(user.getId());

        // then
        assertThat(points).isEqualTo(25L);
    }
}
