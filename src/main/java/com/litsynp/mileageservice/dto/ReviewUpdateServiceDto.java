package com.litsynp.mileageservice.dto;

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
public class ReviewUpdateServiceDto {

    private UUID reviewId;
    private UUID userId;
    private UUID placeId;
    private Set<UUID> attachedPhotoIds;
    private String content;
}
