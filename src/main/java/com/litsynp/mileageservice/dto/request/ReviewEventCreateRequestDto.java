package com.litsynp.mileageservice.dto.request;

import com.litsynp.mileageservice.dto.service.ReviewCreateServiceDto;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEventCreateRequestDto extends ReviewEventRequestDto {

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
