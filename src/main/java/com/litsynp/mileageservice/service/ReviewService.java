package com.litsynp.mileageservice.service;

import com.litsynp.mileageservice.dao.PhotoRepository;
import com.litsynp.mileageservice.dao.PlaceRepository;
import com.litsynp.mileageservice.dao.ReviewRepository;
import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.dao.UserRepository;
import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.domain.UserPoint;
import com.litsynp.mileageservice.dto.service.ReviewCreateServiceDto;
import com.litsynp.mileageservice.dto.service.ReviewUpdateServiceDto;
import com.litsynp.mileageservice.global.error.exception.DuplicateResourceException;
import com.litsynp.mileageservice.global.error.exception.NotFoundException;
import com.litsynp.mileageservice.global.error.exception.NotFoundFieldException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;

    @Transactional
    public Review writeReview(ReviewCreateServiceDto dto) {
        // Find if user exists
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundFieldException(
                        User.class.getSimpleName(),
                        "id",
                        dto.getUserId().toString()));

        // Find if place exists
        Place place = placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new NotFoundFieldException(
                        Place.class.getSimpleName(),
                        "id",
                        dto.getUserId().toString()));

        if (reviewRepository.existsByUserIdAndPlaceId(dto.getUserId(), dto.getPlaceId())) {
            // 한 사용자는 장소마다 리뷰를 1개만 작성할 수 있다
            throw new DuplicateResourceException(Review.class.getSimpleName());
        }

        Review review = Review.builder()
                .id(dto.getReviewId())
                .user(user)
                .place(place)
                .content(dto.getContent())
                .build();

        // Add photos in the DTO
        for (UUID attachedPhotoId : dto.getAttachedPhotoIds()) {
            Photo attachedPhoto = photoRepository.findById(attachedPhotoId)
                    .orElseThrow(() -> new NotFoundFieldException(
                            Photo.class.getSimpleName(), "id", attachedPhotoId.toString()));
            review.addPhoto(attachedPhoto);
        }

        reviewRepository.save(review);

        long amount = 0L;
        if (StringUtils.hasText(dto.getContent())) {
            // 1자 이상 텍스트 작성: 1점
            amount++;
        }

        if (!review.getAttachedPhotos().isEmpty()) {
            // 1장 이상 사진 첨부: 1점
            amount++;
        }

        if (!reviewRepository.existsByIdNotAndPlaceId(review.getId(), place.getId())) {
            /*
             * 특정 장소에 첫 리뷰 작성: 1점
             * 사용자 입장에서 본 '첫 리뷰'일 때 보너스 점수 부여
             * - 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하고, 삭제된 이후 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여합니다.
             * - 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하는데, 삭제되기 이전 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여하지 않습니다
             */
            amount++;
        }

        if (amount > 0L) {
            userPointRepository.save(new UserPoint(UUID.randomUUID(), user, review, amount));
        }

        return review;
    }

    @Transactional
    public Review updateReview(UUID reviewId, ReviewUpdateServiceDto dto) {
        // Find existing review
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundFieldException(
                        Review.class.getSimpleName(),
                        "id",
                        reviewId.toString()));

        // 사진이 원래 없었는지
        boolean emptyPhotosBefore = existing.getAttachedPhotos().isEmpty();

        // Remove existing photos that are not in the DTO
        existing.getAttachedPhotos()
                .stream()
                .filter(photo -> !dto.getAttachedPhotoIds().contains(photo.getId()))
                .collect(Collectors.toList())
                .forEach(existing::deletePhoto);

        // Add photos in the DTO
        for (UUID attachedPhotoId : dto.getAttachedPhotoIds()) {
            Photo attachedPhoto = photoRepository.findById(attachedPhotoId)
                    .orElseThrow(() -> new NotFoundFieldException(
                            Photo.class.getSimpleName(), "id", attachedPhotoId.toString()));
            existing.addPhoto(attachedPhoto);
        }

        // 리뷰를 수정하면 수정한 내용에 맞는 내용 점수를 계산하여 점수를 부여하거나 회수합니다.
        if (emptyPhotosBefore && !existing.getAttachedPhotos().isEmpty()) {
            // 글만 작성한 리뷰에 사진을 추가하면 1점을 부여합니다.
            UserPoint point = new UserPoint(UUID.randomUUID(), existing.getUser(), existing, 1L);
            userPointRepository.save(point);
        }

        if (!emptyPhotosBefore && existing.getAttachedPhotos().isEmpty()) {
            // 글과 사진이 있는 리뷰에서 사진을 모두 삭제하면 1점을 회수합니다.
            Long userPoints = userPointRepository.getAllUserPoints(existing.getUser().getId());
            if (userPoints > 0) {
                // 1점 이상 있다면, 1점 차감
                userPointRepository.save(
                        new UserPoint(UUID.randomUUID(), existing.getUser(), existing, -1L));
            }
        }

        existing.update(existing.getUser(), existing.getPlace(), dto.getContent());

        return existing;
    }

    @Transactional
    public void deleteReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(Review.class.getSimpleName(), reviewId));

        // 리뷰를 삭제하면 해당 리뷰로 부여한 내용 점수와 보너스 점수 회수
        // 하지만 기록 유지를 위해 삭제해서는 안된다.
        Long pointsFromReview = userPointRepository.getUserPointsFromReview(
                review.getUser().getId(), reviewId);

        // 해당 리뷰로부터 얻은 점수를 계산하여 회수한다.
        if (pointsFromReview > 0L) {
            userPointRepository.save(
                    new UserPoint(UUID.randomUUID(), review.getUser(), review, -pointsFromReview));
        }

        for (UserPoint point : userPointRepository.findByReviewId(reviewId)) {
            point.setReview(null);
        }
        reviewRepository.deleteById(reviewId);
    }
}
