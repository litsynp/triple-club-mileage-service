package com.litsynp.mileageservice.dto.response;

import com.litsynp.mileageservice.domain.UserPoint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPointHistoryResponseDto {

    private UUID id;
    private UUID userId;
    private UUID reviewId;
    private Long amount;
    private LocalDateTime createdOn;

    public static UserPointHistoryResponseDto from(UserPoint point) {
        UUID reviewId = null;
        if (point.getReview() != null) {
            reviewId = point.getReview().getId();
        }

        return UserPointHistoryResponseDto.builder()
                .id(point.getId())
                .userId(point.getUserId())
                .reviewId(reviewId)
                .amount(point.getAmount())
                .createdOn(point.getCreatedOn())
                .build();
    }
}
