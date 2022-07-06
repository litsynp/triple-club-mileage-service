package com.litsynp.mileageservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.litsynp.mileageservice.config.QuerydslConfig;
import com.litsynp.mileageservice.dao.PhotoRepository;
import com.litsynp.mileageservice.dao.PlaceRepository;
import com.litsynp.mileageservice.dao.ReviewRepository;
import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.dao.UserRepository;
import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.dto.ReviewCreateServiceDto;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
@Import({QuerydslConfig.class, ReviewService.class})
@DisplayName("리뷰 Service")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Nested
    @DisplayName("Write review")
    class WriteReviewTest {

        @Test
        @DisplayName("Write review with non-empty text, 0 photo for new place :: Get 1 point for text, 1 point for new place")
        void writeReview_shouldGet_1PointForText_1PointForNewPlace() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(2L);
        }

        @Test
        @DisplayName("Write review with empty text, 2 photos for new place :: Get 1 point for image, 1 point for new place")
        void writeReview_shouldGet_1PointForImage_1PointForNewPlace() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            Set<Photo> photos = Set.of(
                    new Photo(UUID.randomUUID(), "abc1.jpg", "https://example.com/abc1.jpg"),
                    new Photo(UUID.randomUUID(), "abc2.jpg", "https://example.com/abc2.jpg")
            );
            photoRepository.saveAll(photos);

            UUID reviewId = UUID.randomUUID();
            Set<UUID> attachedPhotoIds = photos
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toSet());

            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(2L);
        }

        @Test
        @DisplayName("Write review with non-empty text, 2 photos for new place :: Get 1 point for text, 1 point for image, 1 point for new place")
        void writeReview_shouldGet_1PointForText_1PointForImage_1PointForNewPlace() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            Set<Photo> photos = Set.of(
                    new Photo(UUID.randomUUID(), "abc1.jpg", "https://example.com/abc1.jpg"),
                    new Photo(UUID.randomUUID(), "abc2.jpg", "https://example.com/abc2.jpg")
            );
            photoRepository.saveAll(photos);

            UUID reviewId = UUID.randomUUID();
            Set<UUID> attachedPhotoIds = photos
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toSet());

            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("좋아요!")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(3L);
        }

        @Test
        @DisplayName("Write review with non-empty text for non-new place :: Get 1 point for text")
        void writeReview_shouldGet_1PointForText() {
            // given
            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            // User A
            User userA = new User(UUID.randomUUID(), "testa@example.com", "12345678");
            userRepository.save(userA);

            Review reviewA = new Review(UUID.randomUUID(), userA, place, "좋아요!");
            reviewRepository.save(reviewA);

            // User B
            User userB = new User(UUID.randomUUID(), "testb@example.com", "12345678");
            userRepository.save(userB);

            UUID reviewId = UUID.randomUUID();

            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userB.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getUserPoints(userB.getId());
            assertThat(userPoints).isEqualTo(1L);
        }
    }
}
