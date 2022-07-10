package com.litsynp.mileageservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.litsynp.mileageservice.dto.ReviewUpdateServiceDto;
import com.litsynp.mileageservice.error.exception.DuplicateResourceException;
import java.util.HashSet;
import java.util.List;
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
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
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
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
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
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
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
            Long userPoints = userPointRepository.getAllUserPoints(userB.getId());
            assertThat(userPoints).isEqualTo(1L);
        }

        @Test
        @DisplayName("Write 2 reviews on the same place by 1 user :: Rejected")
        void writeReview_1User2ReviewOnSamePlace_rejected() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build());

            // when & then
            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("두 번째 리뷰")
                    .build();

            DuplicateResourceException thrown = assertThrows(
                    DuplicateResourceException.class, () ->
                            reviewService.writeReview(dto));
            assertThat(thrown.getMessage())
                    .isEqualTo("Review already exists with the same information");
        }
    }

    @Nested
    @DisplayName("Update review")
    class UpdateReviewTest {

        @Test
        @DisplayName("Update review :: 0 photos, 1+ content previously - Add photos :: +1 point")
        void updateReview_0PhotosPlus1ContentPreviously_addPhotos_add1Point() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Write review first - 2 points (1 point for content, 1 point for new review)
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build());

            // Update review with new images
            Set<Photo> photos = Set.of(
                    new Photo(UUID.randomUUID(), "abc1.jpg", "https://example.com/abc1.jpg"),
                    new Photo(UUID.randomUUID(), "abc2.jpg", "https://example.com/abc2.jpg")
            );
            photoRepository.saveAll(photos);

            Set<UUID> attachedPhotoIds = photos
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toSet());

            ReviewUpdateServiceDto dto = ReviewUpdateServiceDto.builder()
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("좋아요!")
                    .build();

            // when
            reviewService.updateReview(review.getId(), dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(3L);
        }

        @Test
        @DisplayName("Update review :: 0 photos, 1+ content previously - Change content :: +0 point")
        void updateReview_0PhotoPlus1ContentPreviously_changeContent_add0Point() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Write review first - 2 points (1 point for content, 1 point for new review)
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build());

            // Update review
            ReviewUpdateServiceDto dto = ReviewUpdateServiceDto.builder()
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.updateReview(review.getId(), dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(2L);
        }

        @Test
        @DisplayName("Update review :: 0 photos, 0 content previously - Add content :: +0 point")
        void updateReview_0Photos0ContentPreviously_changeContentAndAddPhotos_remove1Point() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Write review first - 1 point for new review
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("")
                    .build());

            // Update review
            ReviewUpdateServiceDto dto = ReviewUpdateServiceDto.builder()
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.updateReview(review.getId(), dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(1L);
        }

        @Test
        @DisplayName("Update review :: 2 photos, 1+ content previously - Remove 1 photo :: -0 point")
        void updateReview_2PhotosPlus1ContentPreviously_remove1Photo_remove0Point() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Update review with new images - 1 point for content, 1 point for image, 1 point for new review
            List<Photo> photos = List.of(
                    new Photo(UUID.randomUUID(), "abc1.jpg", "https://example.com/abc1.jpg"),
                    new Photo(UUID.randomUUID(), "abc2.jpg", "https://example.com/abc2.jpg")
            );
            photoRepository.saveAll(photos);

            Set<UUID> attachedPhotoIds = photos
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toSet());

            // Write review first - 1 point for new review
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("좋아요!")
                    .build());

            // Update review
            ReviewUpdateServiceDto dto = ReviewUpdateServiceDto.builder()
                    .attachedPhotoIds(Set.of(photos.get(0).getId()))
                    .content("좋아요!")
                    .build();

            // when
            reviewService.updateReview(review.getId(), dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(3L);
        }

        @Test
        @DisplayName("Update review :: 2 photos, 1+ content previously - Remove all photos :: -1 point")
        void updateReview_2PhotosPlus1ContentPreviously_removeAllPhotos_remove1Point() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Update review with new images - 1 point for content, 1 point for image, 1 point for new review
            List<Photo> photos = List.of(
                    new Photo(UUID.randomUUID(), "abc1.jpg", "https://example.com/abc1.jpg"),
                    new Photo(UUID.randomUUID(), "abc2.jpg", "https://example.com/abc2.jpg")
            );
            photoRepository.saveAll(photos);

            Set<UUID> attachedPhotoIds = photos
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toSet());

            // Write review first - 1 point for new review
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("좋아요!")
                    .build());

            // Update review
            ReviewUpdateServiceDto dto = ReviewUpdateServiceDto.builder()
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.updateReview(review.getId(), dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(user.getId());
            assertThat(userPoints).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("Delete review by ID")
    class DeleteReviewTest {

        @Test
        @DisplayName("Delete review :: All photos and points removed")
        void deleteReview_allPhotosAndPointsRemoved() {
            // given
            User user = new User(UUID.randomUUID(), "test@example.com", "12345678");
            userRepository.save(user);

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Update review with new images - 1 point for content, 1 point for image, 1 point for new review
            List<Photo> photos = List.of(
                    new Photo(UUID.randomUUID(), "abc1.jpg", "https://example.com/abc1.jpg"),
                    new Photo(UUID.randomUUID(), "abc2.jpg", "https://example.com/abc2.jpg")
            );
            photoRepository.saveAll(photos);

            Set<UUID> attachedPhotoIds = photos
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toSet());

            // Write review first - 1 point for new review
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(user.getId())
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("좋아요!")
                    .build());

            // when
            reviewService.deleteReviewById(review.getId());

            // then
            assertThat(reviewRepository.findById(reviewId)).isEmpty();

            // 총 2개 기록 = 작성 시 추가 기록 1 (첫 리뷰 1 + 내용 1 + 사진 1) + 삭제 시 회수 기록 1개
            assertThat(userPointRepository.findAll()).hasSize(2);

            assertThat(userPointRepository.getAllUserPoints(user.getId())).isZero();
            assertThat(photoRepository.findAll()).isEmpty();
        }
    }
}
