package com.litsynp.mileageservice.dto.service;

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
public class ReviewCreateServiceDto {

    private UUID reviewId;
    private UUID userId;
    private UUID placeId;
    private Set<UUID> attachedPhotoIds;
    private String content;
}
