package com.litsynp.mileageservice.api;

import com.litsynp.mileageservice.dto.ReviewEventCreateRequestDto;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventApiController {

    @PostMapping
    private ResponseEntity<Void> writeReview(@Valid @RequestBody ReviewEventCreateRequestDto dto) {
        // TODO: handle write-review event
        return ResponseEntity.noContent().build();
    }
}
