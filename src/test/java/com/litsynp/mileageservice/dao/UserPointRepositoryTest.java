package com.litsynp.mileageservice.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.UserPoint;
import com.litsynp.mileageservice.global.config.QuerydslConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(QuerydslConfig.class)
@DisplayName("사용자 포인트 Repository")
class UserPointRepositoryTest {

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("사용자 포인트 전체 총점 조회")
    void getAllUserPoints() {
        // given
        UUID userId = UUID.randomUUID();

        Place place = new Place(UUID.randomUUID(), "해운대 수변공원");
        placeRepository.save(place);

        Review review = new Review(UUID.randomUUID(), userId, place, "또 방문하고 싶어요!");
        reviewRepository.save(review);

        List<UserPoint> userPoints = List.of(
                new UserPoint(UUID.randomUUID(), userId, review, 10L),
                new UserPoint(UUID.randomUUID(), userId, review, -15L),
                new UserPoint(UUID.randomUUID(), userId, review, 30L)
        );
        userPointRepository.saveAll(userPoints);

        // when
        Long points = userPointRepository.getAllUserPoints(userId);

        // then
        assertThat(points).isEqualTo(25L);
    }

    @Test
    @DisplayName("리뷰로부터 발생한 사용자 포인트 총점 조회 :: OK")
    void getUserPointsFromReview() {
        // given
        // 리뷰 작성
        UUID userId = UUID.randomUUID();

        Place place = new Place(UUID.randomUUID(), "해운대 수변공원");
        placeRepository.save(place);

        Review review = new Review(UUID.randomUUID(), userId, place, "또 방문하고 싶어요!");
        reviewRepository.save(review);

        // 같은 사용자가 다른 장소에 리뷰 작성 - 새로운 리뷰 생성
        Place place2 = new Place(UUID.randomUUID(), "광안리 수변공원");
        placeRepository.save(place2);

        Review review2 = new Review(UUID.randomUUID(), userId, place2, "좋아요!");
        reviewRepository.save(review2);

        // 각 리뷰의 포인트 저장
        List<UserPoint> userPoints = List.of(
                new UserPoint(UUID.randomUUID(), userId, review, 2L),
                new UserPoint(UUID.randomUUID(), userId, review2, 1L)
        );
        userPointRepository.saveAll(userPoints);

        // when
        Long points = userPointRepository.getUserPointsFromReview(userId, review.getId());

        // then
        assertThat(points).isEqualTo(2L);
    }

    @Test
    @DisplayName("사용자 ID 및 리뷰 ID로 포인트 기록 검색 :: 2개 중 사용자의 기록 1개만 조회 :: OK")
    void search_ok() {
        // given
        // 리뷰 작성
        UUID userId = UUID.randomUUID();

        Place place = new Place(UUID.randomUUID(), "해운대 수변공원");
        placeRepository.save(place);

        Review review = new Review(UUID.randomUUID(), userId, place, "또 방문하고 싶어요!");
        reviewRepository.save(review);

        // 같은 사용자가 다른 장소에 리뷰 작성 - 새로운 리뷰 생성
        Place place2 = new Place(UUID.randomUUID(), "광안리 수변공원");
        placeRepository.save(place2);

        Review review2 = new Review(UUID.randomUUID(), userId, place2, "좋아요!");
        reviewRepository.save(review2);

        // 각 리뷰의 포인트 저장
        List<UserPoint> userPoints = List.of(
                new UserPoint(UUID.randomUUID(), userId, review, 2L),
                new UserPoint(UUID.randomUUID(), userId, review2, 1L)
        );
        userPointRepository.saveAll(userPoints);

        // when
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserPoint> searchResult = userPointRepository.search(pageRequest, userId,
                review.getId());

        // then
        assertThat(searchResult).hasSize(1);
    }
}
