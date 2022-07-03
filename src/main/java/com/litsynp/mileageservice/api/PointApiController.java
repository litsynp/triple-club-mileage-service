package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.dto.UserPointResponseDto;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
public class PointApiController {

    @GetMapping
    public ResponseEntity<UserPointResponseDto> getPoints(@RequestParam("user-id") String userId) {
        // TODO: retrieve user points with aggregate
        UserPointResponseDto response = UserPointResponseDto.builder()
                .userId(UUID.randomUUID())
                .points(0L)
                .build();
        return ResponseEntity.ok(response);
    }
}
