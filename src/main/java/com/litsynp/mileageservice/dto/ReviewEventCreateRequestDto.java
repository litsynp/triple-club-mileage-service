package com.litsynp.mileageservice.dto;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewEventCreateRequestDto {

    private String type;
    private String action;
    private String reviewId;
    private Set<String> attatchedPhotoIds;
    private String userId;
    private String placeId;
}
