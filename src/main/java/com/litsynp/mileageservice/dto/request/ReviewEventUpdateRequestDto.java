package com.litsynp.mileageservice.dto.request;

import com.litsynp.mileageservice.dto.service.ReviewUpdateServiceDto;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ReviewEventUpdateRequestDto extends ReviewEventRequestDto {

    @NotNull(message = "attached photo ID list cannot be null")
    private Set<UUID> attachedPhotoIds;

    @NotNull(message = "content cannot be null")
    private String content;

    public ReviewUpdateServiceDto toServiceDto() {
        return ReviewUpdateServiceDto.builder()
                .attachedPhotoIds(attachedPhotoIds)
                .content(content)
                .build();
    }
}
