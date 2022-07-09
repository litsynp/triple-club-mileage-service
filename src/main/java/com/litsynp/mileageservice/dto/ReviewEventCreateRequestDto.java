package com.litsynp.mileageservice.dto;

import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEventCreateRequestDto {

    @NotEmpty(message = "type cannot be empty")
    private String type;

    @NotEmpty(message = "action must be either [ADD, MOD, DELETE]")
    private String action;

    @NotNull(message = "review ID cannot be null")
    private UUID reviewId;

    @NotNull(message = "user ID cannot be null")
    private UUID userId;

    @NotNull(message = "place ID cannot be null")
    private UUID placeId;

    @NotNull(message = "attached photo ID list cannot be null")
    private Set<UUID> attachedPhotoIds;

    @NotNull(message = "content cannot be null")
    private String content;

    public ReviewCreateServiceDto toServiceDto() {
        return ReviewCreateServiceDto.builder()
                .reviewId(reviewId)
                .userId(userId)
                .placeId(placeId)
                .attachedPhotoIds(attachedPhotoIds)
                .content(content)
                .build();
    }
}
