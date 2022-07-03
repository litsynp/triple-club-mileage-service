package com.litsynp.mileageservice.service;

import com.litsynp.mileageservice.dao.PlaceRepository;
import com.litsynp.mileageservice.dao.ReviewRepository;
import com.litsynp.mileageservice.dao.UserRepository;
import com.litsynp.mileageservice.domain.Place;
import com.litsynp.mileageservice.domain.Review;
import com.litsynp.mileageservice.domain.User;
import com.litsynp.mileageservice.dto.ReviewCreateServiceDto;
import com.litsynp.mileageservice.dto.ReviewUpdateServiceDto;
import com.litsynp.mileageservice.error.exception.NotFoundException;
import com.litsynp.mileageservice.error.exception.NotFoundFieldException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

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

        Review review = Review.builder()
                .id(dto.getReviewId())
                .user(user)
                .place(place)
                .content(dto.getContent())
                .build();

        /*
         * TODO
         *  사용자 입장에서 본 '첫 리뷰'일 때 보너스 점수 부여
         *  - 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하고, 삭제된 이후 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여합니다.
         *  - 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하는데, 삭제되기 이전 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여하지 않습니다
         */

        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(ReviewUpdateServiceDto dto) {
        // Find existing review
        Review existing = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundFieldException(
                        Review.class.getSimpleName(),
                        "id",
                        dto.getUserId().toString()));

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

        /*
         * TODO
         *  리뷰를 수정하면 수정한 내용에 맞는 내용 점수를 계산하여 점수를 부여하거나 회수합니다.
         *  - 글만 작성한 리뷰에 사진을 추가하면 1점을 부여합니다.
         *  - 글과 사진이 있는 리뷰에서 사진을 모두 삭제하면 1점을 회수합니다.
         */

        existing.update(user, place, dto.getContent());

        return existing;
    }

    @Transactional
    public void deleteReview(UUID reviewId) {
        if (reviewRepository.findById(reviewId).isEmpty()) {
            throw new NotFoundException(Review.class.getSimpleName(), reviewId);
        }

        // TODO: 리뷰를 삭제하면 해당 리뷰로 부여한 내용 점수와 보너스 점수 회수

        reviewRepository.deleteById(reviewId);
    }
}
