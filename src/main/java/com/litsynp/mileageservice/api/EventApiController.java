package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.dto.ReviewEventCreateRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventCreateResponseDto;
import com.litsynp.mileageservice.dto.ReviewEventDeleteRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventUpdateRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventUpdateResponseDto;
import com.litsynp.mileageservice.error.ErrorCode;
import com.litsynp.mileageservice.error.exception.BusinessException;
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
        String type = dto.getType();
        String action = dto.getAction();

        if ("REVIEW".equals(type)) {
            if ("ADD".equals(action)) {
                ReviewEventCreateRequestDto createDto = (ReviewEventCreateRequestDto) dto;

                ReviewEventCreateResponseDto response = ReviewEventCreateResponseDto.from(
                        reviewService.writeReview(createDto.toServiceDto()));
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(response);
            } else if ("MOD".equals(action)) {
                ReviewEventUpdateRequestDto updateDto = (ReviewEventUpdateRequestDto) dto;

                ReviewEventUpdateResponseDto response = ReviewEventUpdateResponseDto.from(
                        reviewService.updateReview(updateDto.getReviewId(),
                                updateDto.toServiceDto()));
                return ResponseEntity.ok(response);
            } else if ("DELETE".equals(action)) {
                ReviewEventDeleteRequestDto deleteDto = (ReviewEventDeleteRequestDto) dto;

                reviewService.deleteReviewById(deleteDto.getReviewId());
                return ResponseEntity.noContent().build();
            }
        }

        throw new BusinessException("Event and action not supported",
                ErrorCode.INVALID_INPUT_VALUE);
    }
}
