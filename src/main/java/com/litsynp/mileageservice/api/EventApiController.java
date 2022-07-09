package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.dto.ReviewEventCreateRequestDto;
import com.litsynp.mileageservice.dto.ReviewEventCreateResponseDto;
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
    private ResponseEntity<?> handleEvent(@Valid @RequestBody ReviewEventCreateRequestDto dto) {
        String type = dto.getType();
        String action = dto.getAction();

        if ("REVIEW".equals(type)) {
            if ("ADD".equals(action)) {
                ReviewEventCreateResponseDto response = ReviewEventCreateResponseDto.from(
                        reviewService.writeReview(dto.toServiceDto()));
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(response);
            }
        }

        throw new BusinessException("Event and action not supported", ErrorCode.INVALID_INPUT_VALUE);
    }
}
