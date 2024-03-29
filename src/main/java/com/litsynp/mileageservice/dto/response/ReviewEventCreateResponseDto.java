package com.litsynp.mileageservice.dto.response;

import static java.util.stream.Collectors.toSet;

import com.litsynp.mileageservice.domain.Photo;
import com.litsynp.mileageservice.domain.Review;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEventCreateResponseDto {

    private UUID id;
    private UUID userId;
    private UUID placeId;
    private Set<UUID> attachedPhotoIds = new HashSet<>();
    private String content;

    public static ReviewEventCreateResponseDto from(Review review) {
        Set<UUID> attachedPhotoIds = review.getAttachedPhotos()
                .stream()
                .map(Photo::getId)
                .collect(toSet());

        return ReviewEventCreateResponseDto.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .placeId(review.getPlaceId())
                .attachedPhotoIds(attachedPhotoIds)
                .content(review.getContent())
                .build();
    }
}
