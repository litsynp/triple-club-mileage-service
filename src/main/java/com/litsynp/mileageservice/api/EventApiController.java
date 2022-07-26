package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.domain.EventType;
import com.litsynp.mileageservice.domain.ReviewAction;
import com.litsynp.mileageservice.dto.request.ReviewEventCreateRequestDto;
import com.litsynp.mileageservice.dto.response.ReviewEventCreateResponseDto;
import com.litsynp.mileageservice.dto.request.ReviewEventDeleteRequestDto;
import com.litsynp.mileageservice.dto.request.ReviewEventRequestDto;
import com.litsynp.mileageservice.dto.request.ReviewEventUpdateRequestDto;
import com.litsynp.mileageservice.dto.response.ReviewEventUpdateResponseDto;
import com.litsynp.mileageservice.global.error.ErrorCode;
import com.litsynp.mileageservice.global.error.exception.BusinessException;
import com.litsynp.mileageservice.service.ReviewService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventApiController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> handleEvent(@Valid @RequestBody ReviewEventRequestDto dto) {
        ReviewAction action = ReviewAction.valueOf(dto.getAction());

        switch (action) {
            case ADD:
                ReviewEventCreateRequestDto createDto = (ReviewEventCreateRequestDto) dto;
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ReviewEventCreateResponseDto.from(
                                reviewService.writeReview(createDto.toServiceDto())));
            case MOD:
                ReviewEventUpdateRequestDto updateDto = (ReviewEventUpdateRequestDto) dto;
                return ResponseEntity.ok(
                        ReviewEventUpdateResponseDto.from(
                                reviewService.updateReview(updateDto.getReviewId(),
                                        updateDto.toServiceDto())));
            case DELETE:
                ReviewEventDeleteRequestDto deleteDto = (ReviewEventDeleteRequestDto) dto;
                reviewService.deleteReviewById(deleteDto.getReviewId());
                return ResponseEntity.noContent().build();
            default:
                throw new BusinessException("Event and action not supported",
                        ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
