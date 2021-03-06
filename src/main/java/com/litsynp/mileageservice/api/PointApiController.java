package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.dto.response.UserPointTotalResponseDto;
import com.litsynp.mileageservice.service.UserPointService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointApiController {

    private final UserPointService userPointService;

    @GetMapping
    public ResponseEntity<UserPointTotalResponseDto> getPoints(
            @RequestParam("user-id") String userId) {
        UUID uuidUserId = UUID.fromString(userId);
        Long points = userPointService.getUserPoints(uuidUserId);

        UserPointTotalResponseDto response = UserPointTotalResponseDto.builder()
                .userId(uuidUserId)
                .points(points)
                .build();
        return ResponseEntity.ok(response);
    }
}
