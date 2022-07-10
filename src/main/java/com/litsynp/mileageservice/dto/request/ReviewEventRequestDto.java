package com.litsynp.mileageservice.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "action", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(name = "ADD", value = ReviewEventCreateRequestDto.class),
        @JsonSubTypes.Type(name = "MOD", value = ReviewEventUpdateRequestDto.class),
        @JsonSubTypes.Type(name = "DELETE", value = ReviewEventDeleteRequestDto.class),})
public class ReviewEventRequestDto {

    @NotEmpty(message = "type cannot be empty")
    protected String type;

    @NotEmpty(message = "action must be either [ADD, MOD, DELETE]")
    protected String action;

    @NotNull(message = "review ID cannot be null")
    protected UUID reviewId;
}
