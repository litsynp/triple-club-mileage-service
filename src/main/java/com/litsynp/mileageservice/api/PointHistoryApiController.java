package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.dto.response.UserPointHistoryResponseDto;
import com.litsynp.mileageservice.service.UserPointService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point-histories")
@RequiredArgsConstructor
public class PointHistoryApiController {

    private final UserPointService userPointService;

    @GetMapping
    public ResponseEntity<Page<UserPointHistoryResponseDto>> getPoints(
            @PageableDefault(sort = "createdOn", direction = Direction.DESC) Pageable pageable,
            @RequestParam(value = "user-id", required = false) UUID userId,
            @RequestParam(value = "review-id", required = false) UUID reviewId) {
        Page<UserPointHistoryResponseDto> response = userPointService
                .search(pageable, userId, reviewId)
                .map(UserPointHistoryResponseDto::from);

        return ResponseEntity.ok(response);
    }
}
