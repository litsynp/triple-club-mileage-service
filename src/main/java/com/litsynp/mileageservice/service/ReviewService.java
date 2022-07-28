package com.litsynp.mileageservice.service;

import com.litsynp.mileageservice.dao.PhotoRepository;
import com.litsynp.mileageservice.dao.PlaceRepository;
import com.litsynp.mileageservice.dao.ReviewRepository;
import com.litsynp.mileageservice.dao.UserPointRepository;
import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
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
    private final UserPointRepository userPointRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;

    @Transactional
    public Review writeReview(ReviewCreateServiceDto dto) {
        // 장소가 존재하는지 확인
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
                .userId(dto.getUserId())
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

        // 내용 점수 계산
        if (StringUtils.hasText(dto.getContent())) {
            // 1자 이상 텍스트 작성: 1점
            amount++;
        }

        if (!review.getAttachedPhotos().isEmpty()) {
            // 1장 이상 사진 첨부: 1점
            amount++;
        }

        // 보너수 점수 계산
        if (isFirstReviewAtPlace(place, review)) {
            // 특정 장소에 첫 리뷰 작성: 1점
            amount++;
        }

        // 점수가 0 이상일 때만 기록
        if (amount > 0L) {
            userPointRepository.save(
                    new UserPoint(UUID.randomUUID(), dto.getUserId(), review, amount));
        }

        return review;
    }

    /**
     * 해당 장소에서 작성한 첫 리뷰인지 반환
     *
     * @param place  장소
     * @param review 리뷰
     * @return 해당 장소에서 작성한 첫 리뷰인지 여부
     */
    private boolean isFirstReviewAtPlace(Place place, Review review) {
        return !reviewRepository.existsByIdNotAndPlaceId(review.getId(), place.getId());
    }

    @Transactional
    public Review updateReview(UUID reviewId, ReviewUpdateServiceDto dto) {
        // 기존의 리뷰 확인
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundFieldException(
                        Review.class.getSimpleName(),
                        "id",
                        reviewId.toString()));

        // 사진이 원래 없었는지 확인
        boolean emptyPhotosBefore = existing.getAttachedPhotos().isEmpty();

        // DTO에 들어있지 않은 기존의 이미지 삭제
        existing.getAttachedPhotos()
                .stream()
                .filter(photo -> !dto.getAttachedPhotoIds().contains(photo.getId()))
                .collect(Collectors.toList())
                .forEach(existing::deletePhoto);

        // DTO에 들어있는 새로운 이미지 추가
        for (UUID attachedPhotoId : dto.getAttachedPhotoIds()) {
            Photo attachedPhoto = photoRepository.findById(attachedPhotoId)
                    .orElseThrow(() -> new NotFoundFieldException(
                            Photo.class.getSimpleName(), "id", attachedPhotoId.toString()));
            existing.addPhoto(attachedPhoto);
        }

        // 리뷰를 수정하면 수정한 내용에 맞는 내용 점수를 계산하여 점수를 부여하거나 회수합니다.
        if (emptyPhotosBefore && !existing.getAttachedPhotos().isEmpty()) {
            // 글만 작성한 리뷰에 사진을 추가하면 1점을 부여합니다.
            UserPoint point = new UserPoint(UUID.randomUUID(), existing.getUserId(), existing, 1L);
            userPointRepository.save(point);
        }

        if (!emptyPhotosBefore && existing.getAttachedPhotos().isEmpty()) {
            // 글과 사진이 있는 리뷰에서 사진을 모두 삭제하면 1점을 회수합니다.
            Long userPoints = userPointRepository.getAllUserPoints(existing.getUserId());
            if (userPoints > 0) {
                // 1점 이상 있다면, 1점 차감
                userPointRepository.save(
                        new UserPoint(UUID.randomUUID(), existing.getUserId(), existing, -1L));
            }
        }

        existing.update(existing.getUserId(), existing.getPlace(), dto.getContent());

        return existing;
    }

    @Transactional
    public void deleteReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(Review.class.getSimpleName(), reviewId));

        // 리뷰를 삭제하면 해당 리뷰로 부여한 내용 점수와 보너스 점수 회수
        // 하지만 기록 유지를 위해 삭제해서는 안된다.
        Long pointsFromReview = userPointRepository.getUserPointsFromReview(
                review.getUserId(), reviewId);

        // 해당 리뷰로부터 얻은 점수를 계산하여 회수한다.
        if (pointsFromReview > 0L) {
            userPointRepository.save(
                    new UserPoint(UUID.randomUUID(), review.getUserId(), review,
                            -pointsFromReview));
        }

        for (UserPoint point : userPointRepository.findByReviewId(reviewId)) {
            point.setReview(null);
        }
        reviewRepository.deleteById(reviewId);
    }
}
