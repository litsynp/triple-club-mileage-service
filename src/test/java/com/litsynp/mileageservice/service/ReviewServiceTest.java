package com.litsynp.mileageservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.litsynp.mileageservice.dao.PhotoRepository;
import com.litsynp.mileageservice.dao.PlaceRepository;
import com.litsynp.mileageservice.dao.ReviewRepository;
import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.dto.service.ReviewCreateServiceDto;
import com.litsynp.mileageservice.dto.service.ReviewUpdateServiceDto;
import com.litsynp.mileageservice.global.config.QuerydslConfig;
import com.litsynp.mileageservice.global.error.exception.DuplicateResourceException;
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
    private PlaceRepository placeRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Nested
    @DisplayName("리뷰 작성")
    class WriteReviewTest {

        @Test
        @DisplayName("글 1자 이상, 사진 0장, 새로운 장소 :: 2점")
        void writeReview_shouldGet_1PointForText_1PointForNewPlace() {
            // given
            UUID userId = UUID.randomUUID();

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userId)
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(2L);
        }

        @Test
        @DisplayName("글 0자, 사진 2장, 새로운 장소 :: 2점")
        void writeReview_shouldGet_1PointForImage_1PointForNewPlace() {
            // given
            UUID userId = UUID.randomUUID();

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
                    .userId(userId)
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(2L);
        }

        @Test
        @DisplayName("글 1자 이상, 사진 2장, 새로운 장소 :: 3점")
        void writeReview_shouldGet_1PointForText_1PointForImage_1PointForNewPlace() {
            // given
            UUID userId = UUID.randomUUID();

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
                    .userId(userId)
                    .placeId(place.getId())
                    .attachedPhotoIds(attachedPhotoIds)
                    .content("좋아요!")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(3L);
        }

        @Test
        @DisplayName("글 1자 이상, 사진 0장, 새로운 장소 아님 :: 1점")
        void writeReview_shouldGet_1PointForText() {
            // given
            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            // User A
            UUID userIdA = UUID.randomUUID();
            Review reviewA = new Review(UUID.randomUUID(), userIdA, place, "좋아요!");
            reviewRepository.save(reviewA);

            // User B
            UUID userIdB = UUID.randomUUID();
            UUID reviewId = UUID.randomUUID();

            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userIdB)
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build();

            // when
            reviewService.writeReview(dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(userIdB);
            assertThat(userPoints).isEqualTo(1L);
        }

        @Test
        @DisplayName("사용자 한 명이 같은 장소에 리뷰 2개 작성 시도 :: 에러")
        void writeReview_1User2ReviewOnSamePlace_rejected() {
            // given
            UUID userId = UUID.randomUUID();

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userId)
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build());

            // when & then
            ReviewCreateServiceDto dto = ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userId)
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
    @DisplayName("리뷰 수정")
    class UpdateReviewTest {

        @Test
        @DisplayName("기존 글 1자 이상, 사진 0장 :: 사진 추가 :: 1점 추가된다")
        void updateReview_0PhotosPlus1ContentPreviously_addPhotos_add1Point() {
            // given
            UUID userId = UUID.randomUUID();

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Write review first - 2 points (1 point for content, 1 point for new review)
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userId)
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
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(3L);
        }

        @Test
        @DisplayName("기존 글 1자 이상, 사진 0장 :: 글 추가 :: 점수 변동 없다")
        void updateReview_0PhotoPlus1ContentPreviously_changeContent_add0Point() {
            // given
            UUID userId = UUID.randomUUID();

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Write review first - 2 points (1 point for content, 1 point for new review)
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userId)
                    .placeId(place.getId())
                    .attachedPhotoIds(new HashSet<>())
                    .content("좋아요!")
                    .build());

            // Update review
            ReviewUpdateServiceDto dto = ReviewUpdateServiceDto.builder()
                    .attachedPhotoIds(new HashSet<>())
                    .content("너무 좋아요!")
                    .build();

            // when
            reviewService.updateReview(review.getId(), dto);

            // then
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(2L);
        }

        @Test
        @DisplayName("기존 글 0자, 사진 0장 :: 글 1자 이상 추가 :: 1점 추가된다")
        void updateReview_0Photos0ContentPreviously_changeContent_get1Point() {
            // given
            UUID userId = UUID.randomUUID();

            Place place = new Place(UUID.randomUUID(), "Place 1");
            placeRepository.save(place);

            UUID reviewId = UUID.randomUUID();

            // Write review first - 1 point for new review
            Review review = reviewService.writeReview(ReviewCreateServiceDto.builder()
                    .reviewId(reviewId)
                    .userId(userId)
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
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(1L);
        }

        @Test
        @DisplayName("기존 글 1자 이상, 사진 2장 :: 사진 1장 삭제 :: 점수 변동 없다")
        void updateReview_2PhotosPlus1ContentPreviously_remove1Photo_remove0Point() {
            // given
            UUID userId = UUID.randomUUID();

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
                    .userId(userId)
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
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(3L);
        }

        @Test
        @DisplayName("기존 글 1자 이상, 사진 2장 :: 사진 일괄 삭제 :: 1점 차감된다")
        void updateReview_2PhotosPlus1ContentPreviously_removeAllPhotos_remove1Point() {
            // given
            UUID userId = UUID.randomUUID();

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
                    .userId(userId)
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
            Long userPoints = userPointRepository.getAllUserPoints(userId);
            assertThat(userPoints).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("리뷰 삭제 by ID")
    class DeleteReviewTest {

        @Test
        @DisplayName("사진은 일괄 삭제, 포인트 기록은 유지된다")
        void deleteReview_allPhotosDeletedAndPointsRemains() {
            // given
            UUID userId = UUID.randomUUID();

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
                    .userId(userId)
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

            assertThat(userPointRepository.getAllUserPoints(userId)).isZero();
            assertThat(photoRepository.findAll()).isEmpty();
        }
    }
}
